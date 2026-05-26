package de.thecodelabs.midi.midi.device.coremidi;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.midi.message.MidiInputPublisher;
import de.thecodelabs.utils.util.OS;

import javax.sound.midi.MidiUnavailableException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link MidiDeviceManager} implementation that uses the CoreMIDI framework
 * directly via the Java FFM API. Only supported on macOS.
 */
public class CoreMidiDeviceManager implements MidiDeviceManager
{
	public CoreMidiDeviceManager()
	{
		if(!OS.isMacOS())
		{
			throw new UnsupportedOperationException(
					"CoreMidiDeviceManager is only supported on macOS");
		}
	}

	@Override
	public Collection<MidiDeviceInfo> listDevices()
	{
		// Device → Entity → Endpoints.
		// Entity names are OS-localized (e.g. German: "Anschluss 1"), so we use the
		// source endpoint name instead — it is set by the device driver and is always
		// descriptive (e.g. "LPMiniMK3 DAW Out"). We strip the trailing direction
		// suffix for the displayName so callers can show a clean label.
		final List<MidiDeviceInfo> result = new ArrayList<>();
		final long numDevices = CoreMidiLibrary.getNumberOfDevices();
		for(long d = 0; d < numDevices; d++)
		{
			final int deviceRef = CoreMidiLibrary.getDevice(d);
			if(CoreMidiLibrary.getIntegerProperty(deviceRef, CoreMidiLibrary.K_MIDI_PROPERTY_OFFLINE) != 0) continue;

			final String manufacturer = CoreMidiLibrary.getStringProperty(deviceRef, CoreMidiLibrary.K_MIDI_PROPERTY_MANUFACTURER);
			final long numEntities = CoreMidiLibrary.getNumberOfEntities(deviceRef);
			for(long e = 0; e < numEntities; e++)
			{
				final int entityRef = CoreMidiLibrary.getEntity(deviceRef, e);
				final String name = endpointName(entityRef);
				if(!name.isEmpty())
				{
					result.add(new MidiDeviceInfo(name, stripDirectionSuffix(name), manufacturer));
				}
			}
		}
		return result;
	}

	@Override
	public MidiDevice openDevice(final MidiDeviceInfo deviceInfo, final Midi.Mode... modes) throws MidiUnavailableException
	{
		// MidiDeviceInfo.name holds the raw source-endpoint name (e.g. "LPMiniMK3 DAW Out").
		// Match against the same derived name used in listDevices().
		final String name = deviceInfo.name();
		int sourceRef = 0;
		int destRef = 0;

		outer:
		for(long d = 0, numDevices = CoreMidiLibrary.getNumberOfDevices(); d < numDevices; d++)
		{
			final int deviceRef = CoreMidiLibrary.getDevice(d);
			for(long e = 0, numEntities = CoreMidiLibrary.getNumberOfEntities(deviceRef); e < numEntities; e++)
			{
				final int entityRef = CoreMidiLibrary.getEntity(deviceRef, e);
				if(!name.equals(endpointName(entityRef))) continue;
				if(CoreMidiLibrary.getNumberOfEntitySources(entityRef) > 0)
				{
					sourceRef = CoreMidiLibrary.getEntitySource(entityRef, 0);
				}
				if(CoreMidiLibrary.getNumberOfEntityDestinations(entityRef) > 0)
				{
					destRef = CoreMidiLibrary.getEntityDestination(entityRef, 0);
				}
				break outer;
			}
		}

		if(sourceRef == 0 && destRef == 0)
		{
			throw new MidiUnavailableException("MIDI device not found: " + name);
		}

		final CoreMidiDevice device = new CoreMidiDevice(new MidiInputPublisher(), deviceInfo, sourceRef, destRef);
		device.open(modes);
		return device;
	}

	@Override
	public void close()
	{
		CoreMidiLibrary.disposeClient();
	}

	/** Returns the source-endpoint name for an entity, falling back to the destination or entity name. */
	private static String endpointName(final int entityRef)
	{
		if(CoreMidiLibrary.getNumberOfEntitySources(entityRef) > 0)
		{
			final String n = CoreMidiLibrary.getStringProperty(
					CoreMidiLibrary.getEntitySource(entityRef, 0), CoreMidiLibrary.K_MIDI_PROPERTY_NAME);
			if(!n.isEmpty()) return n;
		}
		if(CoreMidiLibrary.getNumberOfEntityDestinations(entityRef) > 0)
		{
			final String n = CoreMidiLibrary.getStringProperty(
					CoreMidiLibrary.getEntityDestination(entityRef, 0), CoreMidiLibrary.K_MIDI_PROPERTY_NAME);
			if(!n.isEmpty()) return n;
		}
		return CoreMidiLibrary.getStringProperty(entityRef, CoreMidiLibrary.K_MIDI_PROPERTY_NAME);
	}

	private static String stripDirectionSuffix(final String name)
	{
		if(name.endsWith(" Out")) return name.substring(0, name.length() - 4);
		if(name.endsWith(" In")) return name.substring(0, name.length() - 3);
		return name;
	}
}
