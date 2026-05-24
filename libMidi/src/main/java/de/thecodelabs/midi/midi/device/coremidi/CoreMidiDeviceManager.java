package de.thecodelabs.midi.midi.device.coremidi;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.midi.message.MidiInputPublisher;

import javax.sound.midi.MidiUnavailableException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * {@link MidiDeviceManager} implementation that uses the CoreMIDI framework
 * directly via the Java FFM API. Only supported on macOS.
 */
public class CoreMidiDeviceManager implements MidiDeviceManager
{
	public CoreMidiDeviceManager()
	{
		if(!System.getProperty("os.name", "").startsWith("Mac"))
		{
			throw new UnsupportedOperationException(
					"CoreMidiDeviceManager is only supported on macOS");
		}
	}

	@Override
	public MidiDeviceInfo[] listDevices()
	{
		// Merge sources and destinations by name so each physical device appears once.
		final Map<String, MidiDeviceInfo> byName = new LinkedHashMap<>();

		final long numSources = CoreMidiLibrary.getNumberOfSources();
		for(long i = 0; i < numSources; i++)
		{
			final int ref = CoreMidiLibrary.getSource(i);
			addIfAbsent(byName, ref);
		}

		final long numDests = CoreMidiLibrary.getNumberOfDestinations();
		for(long i = 0; i < numDests; i++)
		{
			final int ref = CoreMidiLibrary.getDestination(i);
			addIfAbsent(byName, ref);
		}

		return byName.values().toArray(new MidiDeviceInfo[0]);
	}

	@Override
	public MidiDevice openDevice(final MidiDeviceInfo deviceInfo, final Midi.Mode... modes) throws MidiUnavailableException
	{
		final String name = deviceInfo.name();
		final int sourceRef = findEndpointByName(name, true);
		final int destRef = findEndpointByName(name, false);

		if(sourceRef == 0 && destRef == 0)
		{
			throw new MidiUnavailableException("MIDI device not found: " + name);
		}

		final CoreMidiDevice device = new CoreMidiDevice(new MidiInputPublisher(), deviceInfo, sourceRef, destRef);
		device.open(modes);
		return device;
	}

	private static void addIfAbsent(final Map<String, MidiDeviceInfo> map, final int endpointRef)
	{
		final String name = CoreMidiLibrary.getStringProperty(endpointRef, CoreMidiLibrary.K_MIDI_PROPERTY_NAME);
		if(name.isEmpty()) return;
		final String manufacturer = CoreMidiLibrary.getStringProperty(endpointRef, CoreMidiLibrary.K_MIDI_PROPERTY_MANUFACTURER);
		map.putIfAbsent(name, new MidiDeviceInfo(name, name, manufacturer));
	}

	private static int findEndpointByName(final String name, final boolean sources)
	{
		final long count = sources ? CoreMidiLibrary.getNumberOfSources() : CoreMidiLibrary.getNumberOfDestinations();
		for(long i = 0; i < count; i++)
		{
			final int ref = sources ? CoreMidiLibrary.getSource(i) : CoreMidiLibrary.getDestination(i);
			final String refName = CoreMidiLibrary.getStringProperty(ref, CoreMidiLibrary.K_MIDI_PROPERTY_NAME);
			if(name.equals(refName)) return ref;
		}
		return 0;
	}
}
