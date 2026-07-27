# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Test Commands

```bash
mvn clean install          # Full build
mvn clean compile          # Compile only
mvn test                   # Run all tests
mvn test -Dtest=MappingTest  # Run a single test class
```

The Surefire plugin requires `--enable-native-access=ALL-UNNAMED` (already configured in pom.xml) for Java 25 FFM support.

## Architecture Overview

**libMidi** is a Java 25 library providing cross-platform MIDI device management and an input-to-action mapping system. It is part of the `jlibs` suite and depends on `libUtils`.

### Three Main Domains

**1. MIDI Core** (`de.thecodelabs.midi.midi.*`)

`Midi` is the main facade. At construction it selects a platform-specific `MidiDeviceManager`:
- macOS → `CoreMidiDeviceManager` (Java 25 Foreign Function & Memory API over native CoreMIDI)
- other → `JavaDeviceManager` (javax.sound.midi)

Each manager produces `MidiDevice` subclasses (`CoreMidiDevice` / `JavaMidiDevice`). Incoming bytes are parsed into `MidiMessage` (type, channel, payload) and broadcast via `MidiInputPublisher` to all registered `MidiMessageListener` subscribers.

**2. Mapping System** (`de.thecodelabs.midi.mapping.*`)

Maps `InputKey` instances to `Action` instances and resolves the right `ActionHandler` to execute them.

- `Mapping` — `Map<InputKey, Action>` (the runtime data structure)
- `MappingRegistry` — fluent builder; registers concrete `Action` and `InputKey` subtypes so Jackson can (de)serialize them polymorphically via `@JsonTypeInfo` / `@JsonTypeName`
- `MappingSerializer` — Jackson-based JSON serialization using the registry
- Two `InputKey` implementations: `MidiInputKey` (note/control + optional feedback colors), `KeyboardInputKey` (JavaFX `KeyCode`)
- `ActionHandlerResolver` — resolves the `ActionHandler` for a given `Action`
- `MidiMappingListener` — `MidiMessageListener` that looks up incoming messages in a `Mapping` and dispatches `KeyInputEvent`s to the handler
- `KeyboardMappingListener` — JavaFX keyboard event equivalent

**3. Events** (`de.thecodelabs.midi.event.*`)

`KeyInputEvent` record (InputKey + `KeyInputType` DOWN/UP) is the unified event type passed to `ActionHandler.execute()`.

### Data Flow (Input → Action)

```
Physical device → CoreMidiDevice / JavaMidiDevice
  → MidiMessage (parsed bytes)
  → MidiInputPublisher.publish()
  → MidiMappingListener.onMessage()
  → Mapping.lookup(MidiInputKey)
  → ActionHandlerResolver.resolve(action)
  → ActionHandler.execute(KeyInputEvent)
```

Output path: `Midi.sendMessage(device, MidiMessage)` → device write.

### MIDI Feedback

`MidiInputKey` carries optional `MidiFeedbackColor` values for default, event, and warning states. `MidiFeedback` (channel + value) is sent back to the device to drive LED or display feedback.

### Jackson Polymorphism

`Action` and `InputKey` subtypes register their `@JsonTypeName` label via `MappingRegistry`. Callers must build a registry with all concrete types before (de)serializing — the serializer uses the registry's `ObjectMapper`. Test fixtures (`keyboard.json`, `pd12.json`) in `src/test/resources/` exercise this.

### Module System

`module-info.java` exports seven packages. The module requires `libUtils`, `javafx.controls` (provided), and Jackson 3.x. JavaFX is optional at runtime — only `KeyboardInputKey` / `KeyboardMappingListener` depend on it.
