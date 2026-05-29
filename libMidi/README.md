# libMidi

A Java 25 library for cross-platform MIDI device management and input-to-action mapping. Part of the [jlibs](https://github.com/thecodelabs/jlibs) suite.

## Requirements

- Java 25+
- macOS (CoreMIDI via FFM API) or any platform (javax.sound.midi fallback)
- Maven

---

## Architecture

The library is organized into three domains:

```
de.thecodelabs.midi
├── event/          KeyInputEvent, KeyInputType (DOWN/UP)
├── mapping/        Mapping, MappingRegistry, MappingSerializer
│   ├── action/     Action, ActionHandler, ActionHandlerResolver
│   ├── feedback/   FeedbackState, FeedbackValue, FeedbackValueWriter
│   ├── input/      InputKey, MidiInputKey, KeyboardInputKey
│   └── listener/  MidiMappingListener, KeyboardMappingListener
└── midi/           Midi (facade)
    ├── device/     MidiDevice, MidiDeviceManager, MidiDeviceInfo
    │   ├── coremidi/  CoreMidiDeviceManager (macOS, FFM)
    │   └── java/      JavaDeviceManager (javax.sound.midi)
    └── message/    MidiMessage, MidiMessageType, MidiInputPublisher
```

### Data Flow

```
Physical device
  └─► CoreMidiDevice / JavaMidiDevice
        └─► MidiMessage (parsed bytes)
              └─► MidiInputPublisher.publish()
                    └─► MidiMappingListener.onMidiMessage()
                          └─► Mapping.lookup(MidiInputKey)
                                └─► ActionHandler.handleAction(KeyInputEvent)
                                      └─► FeedbackValueWriter.write()  (optional)
```

---

## MIDI Core

### `Midi`

The main entry point. Automatically selects the platform-appropriate backend.

```java
try (Midi midi = new Midi()) {
    // Lists all connected MIDI devices
    Collection<MidiDeviceInfo> devices = midi.getMidiDevices();
    devices.forEach(System.out::println);

    // Open a device for input and output
    MidiDevice device = midi.openDevice(
        new MidiDeviceInfo("LPMiniMK3 MIDI Out", "LPMiniMK3", "Novation"),
        Midi.Mode.INPUT, Midi.Mode.OUTPUT
    );

    // Send a raw MIDI message
    device.sendMidiMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, (byte) 60, (byte) 100));
}
```

`Midi` is `AutoCloseable` — use it with try-with-resources to ensure all ports are released.

### `MidiListener`

Attach a `MidiListener` before opening a device to receive lifecycle callbacks:

```java
midi.addMidiListener(new MidiListener() {
    @Override
    public void onDeviceOpen(MidiDevice device) {
        // Send initialization SysEx, set LED states, etc.
    }

    @Override
    public void onFeedbackClear(MidiDevice device) {
        // Reset all LEDs / visual feedback
    }
});
```

### `MidiMessage`

Represents a parsed MIDI message. The constructor that accepts a raw `byte[]` strips the status byte into `messageType` + `channel` and puts the remaining data bytes into `payload`.

```java
// Construct from raw bytes
MidiMessage msg = new MidiMessage(new byte[]{(byte) 0x90, 60, 100});
// → messageType = NOTE_ON, channel = 0, payload = [60, 100]

// Construct explicitly
MidiMessage msg = new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, (byte) 60, (byte) 100);
```

---

## Mapping System

The mapping system connects physical inputs (`InputKey`) to application-defined actions (`Action`) and dispatches them through `ActionHandler` implementations.

### Step 1 — Define your types

Every `Action` and `InputKey` subtype must be annotated with `@JsonTypeName` for serialization:

```java
@JsonTypeName("play")
public record PlayAction(String trackId) implements Action {
    @JsonCreator
    public PlayAction(@JsonProperty("trackId") String trackId) { this.trackId = trackId; }
}

public class PlayActionHandler implements ActionHandler {
    @Override
    public FeedbackState handleAction(KeyInputEvent event, Action action) {
        if (event.keyInputType() == KeyInputType.DOWN) {
            // start playback...
            return MyFeedbackState.PLAYING;
        }
        return MyFeedbackState.IDLE;
    }

    @Override
    public FeedbackState getCurrentState(Action action) {
        return MyFeedbackState.IDLE;
    }
}
```

### Step 2 — Build the registry

`MappingRegistry` is the central configuration object. It registers all known types for serialization and wires `ActionHandler` implementations to their `Action` types:

```java
MappingRegistry registry = new MappingRegistry();

registry.registerAction(PlayAction.class, new PlayActionHandler())
        .registerInputKey(MidiInputKey.class)
        .registerInputKey(KeyboardInputKey.class)
        .registerFeedbackState(MyFeedbackState.class)
        .registerFeedbackValue(MidiInputKey.class, MyFeedbackValue.class, new MyFeedbackWriter(midi));

MappingSerializer serializer = registry.build();
```

`MappingRegistry` implements both `ActionHandlerResolver` and `FeedbackValueWriterResolver`, so the same instance is passed wherever those interfaces are required.

### Step 3 — Load or build a mapping

**From JSON:**
```java
Mapping mapping = serializer.deserialize(Path.of("mapping.json"));
```

**Programmatically:**
```java
Mapping mapping = new Mapping();
mapping.addInputKeyWithAction(
    new MidiInputKey((byte) 36, Map.of(MyFeedbackState.PLAYING, new MyFeedbackValue(5))),
    new PlayAction("track-1")
);
mapping.addInputKeyWithAction(
    new KeyboardInputKey(KeyCode.SPACE, "Space"),
    new PlayAction("track-1")
);
```

**Save to JSON:**
```java
serializer.serialize(mapping, Path.of("mapping.json"));
```

### Step 4 — Connect the listener

```java
MidiDevice device = midi.openDevice(deviceInfo, Midi.Mode.INPUT);

device.getPublisher().addMidiListener(
    new MidiMappingListener(mapping, registry, registry)
);
```

Listeners can be registered with an integer priority. Higher values are called first. The default priority is `0`.

```java
device.getPublisher().addMidiListener(rawLogger, 10);   // called before the mapping listener
device.getPublisher().addMidiListener(new MidiMappingListener(mapping, registry, registry));
```

A higher-priority listener can call `message.consume()` to stop propagation to lower-priority listeners.

For JavaFX keyboard input, add the listener to a scene:

```java
scene.addEventFilter(KeyEvent.ANY,
    new KeyboardMappingListener(mapping, registry)
);
```

---

## Feedback

Feedback sends a signal back to the device (e.g. to light up a pad LED) after an action has been handled. It consists of three parts:

| Part | Role |
|---|---|
| `FeedbackState` | An enum that represents the current state of an action (e.g. `PLAYING`, `IDLE`) |
| `FeedbackValue` | Holds the device-specific feedback data (e.g. a color index) for a given state |
| `FeedbackValueWriter` | Sends the `FeedbackValue` to the device via `Midi` |

### Define the types

```java
@JsonTypeName("my-feedback-state")
public enum MyFeedbackState implements FeedbackState {
    IDLE, PLAYING
}

@JsonTypeName("my-feedback-value")
public record MyFeedbackValue(int colorIndex) implements FeedbackValue {
    @JsonCreator
    public MyFeedbackValue(@JsonProperty("colorIndex") int colorIndex) {
        this.colorIndex = colorIndex;
    }
}

public class MyFeedbackWriter implements FeedbackValueWriter<MidiInputKey, MyFeedbackValue> {
    private final Midi midi;

    public MyFeedbackWriter(Midi midi) { this.midi = midi; }

    @Override
    public void write(MidiInputKey key, MyFeedbackValue value) {
        midi.getDevice().sendMidiMessage(
            new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, key.value(), (byte) value.colorIndex())
        );
    }
}
```

### Register and map

```java
registry.registerFeedbackState(MyFeedbackState.class)
        .registerFeedbackValue(MidiInputKey.class, MyFeedbackValue.class, new MyFeedbackWriter(midi));

// Assign feedback values per state to a key
new MidiInputKey((byte) 36, Map.of(
    MyFeedbackState.IDLE,    new MyFeedbackValue(0),
    MyFeedbackState.PLAYING, new MyFeedbackValue(5)
))
```

When `ActionHandler.handleAction()` returns a `FeedbackState`, `MidiMappingListener` looks up the matching `FeedbackValue` from the key's feedback map and calls the registered `FeedbackValueWriter`.

---

## JSON Format

A mapping file is a JSON array of `{inputKey, action}` entries. Both fields use Jackson polymorphism — the `"type"` property selects the concrete subtype.

**MIDI key with feedback:**
```json
[
  {
    "inputKey": {
      "type": "midi",
      "value": 36,
      "feedbackValues": [
        {
          "state": ["my-feedback-state", "PLAYING"],
          "feedbackValue": { "type": "my-feedback-value", "colorIndex": 5 }
        }
      ]
    },
    "action": { "type": "play", "trackId": "track-1" }
  }
]
```

**Keyboard key:**
```json
[
  {
    "inputKey": { "type": "keyboard", "code": "SPACE", "key": "Space" },
    "action": { "type": "play", "trackId": "track-1" }
  }
]
```

The `"type"` values are the `@JsonTypeName` strings you registered in `MappingRegistry`.

---

## Build

```bash
mvn clean install       # full build + tests
mvn clean compile       # compile only
mvn test                # run tests
mvn test -Dtest=MappingTest  # run a specific test
```

The Surefire plugin passes `--enable-native-access=ALL-UNNAMED` automatically (required for the Java 25 FFM API used by `CoreMidiDeviceManager`).

---

## Platform Notes

| Platform | Backend | Notes |
|---|---|---|
| macOS | `CoreMidiDeviceManager` | Uses Java 25 FFM to call CoreMIDI directly. No JNI, no native library to ship. |
| Other | `JavaDeviceManager` | Wraps `javax.sound.midi`. Full MIDI support, no native dependencies. |

The backend is selected automatically at runtime by `Midi` based on `OS.isMacOS()`.
