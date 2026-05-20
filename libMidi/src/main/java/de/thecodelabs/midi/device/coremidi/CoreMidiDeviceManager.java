package de.thecodelabs.midi.device.coremidi;

import de.thecodelabs.midi.device.MidiDevice;
import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.midi.Midi;

import javax.sound.midi.MidiUnavailableException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link MidiDeviceManager} implementation that uses the CoreMIDI framework
 * directly via the Java FFM API. Only supported on macOS.
 */
public class CoreMidiDeviceManager implements MidiDeviceManager {

    public CoreMidiDeviceManager() {
        if (!System.getProperty("os.name", "").startsWith("Mac")) {
            throw new UnsupportedOperationException(
                    "CoreMidiDeviceManager is only supported on macOS");
        }
    }

    @Override
    public MidiDeviceInfo[] listDevices() {
        // Merge sources and destinations by name so each physical device appears once.
        Map<String, MidiDeviceInfo> byName = new LinkedHashMap<>();

        long numSources = CoreMidiLibrary.getNumberOfSources();
        for (long i = 0; i < numSources; i++) {
            int ref = CoreMidiLibrary.getSource(i);
            addIfAbsent(byName, ref);
        }

        long numDests = CoreMidiLibrary.getNumberOfDestinations();
        for (long i = 0; i < numDests; i++) {
            int ref = CoreMidiLibrary.getDestination(i);
            addIfAbsent(byName, ref);
        }

        return byName.values().toArray(new MidiDeviceInfo[0]);
    }

    @Override
    public MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws MidiUnavailableException {
        String name = deviceInfo.getName();
        int sourceRef = findEndpointByName(name, true);
        int destRef = findEndpointByName(name, false);

        if (sourceRef == 0 && destRef == 0) {
            throw new MidiUnavailableException("MIDI device not found: " + name);
        }

        CoreMidiDevice device = new CoreMidiDevice(deviceInfo, sourceRef, destRef);
        device.open(modes);
        return device;
    }

    private static void addIfAbsent(Map<String, MidiDeviceInfo> map, int endpointRef) {
        String name = CoreMidiLibrary.getStringProperty(endpointRef, CoreMidiLibrary.K_MIDI_PROPERTY_NAME);
        if (name.isEmpty()) return;
        String manufacturer = CoreMidiLibrary.getStringProperty(endpointRef, CoreMidiLibrary.K_MIDI_PROPERTY_MANUFACTURER);
        map.putIfAbsent(name, new MidiDeviceInfo(name, name, manufacturer));
    }

    private static int findEndpointByName(String name, boolean sources) {
        long count = sources ? CoreMidiLibrary.getNumberOfSources() : CoreMidiLibrary.getNumberOfDestinations();
        for (long i = 0; i < count; i++) {
            int ref = sources ? CoreMidiLibrary.getSource(i) : CoreMidiLibrary.getDestination(i);
            String refName = CoreMidiLibrary.getStringProperty(ref, CoreMidiLibrary.K_MIDI_PROPERTY_NAME);
            if (name.equals(refName)) return ref;
        }
        return 0;
    }
}
