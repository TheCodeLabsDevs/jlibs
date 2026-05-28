package de.thecodelabs.midi;

import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.MappingRegistry;
import de.thecodelabs.midi.mapping.MappingSerializer;
import de.thecodelabs.midi.mapping.action.TestAction;
import de.thecodelabs.midi.mapping.action.TestActionHandler;
import de.thecodelabs.midi.mapping.input.MidiInputKey;
import de.thecodelabs.midi.mapping.listener.MidiMappingListener;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.utils.io.IOUtils;

import java.util.Collection;

public class MidiIntegrationTestMain
{
	public static void main(final String[] args)
	{
		try(final Midi midi = new Midi())
		{
			final TestActionHandler actionHandler = new TestActionHandler();

			final MappingRegistry registry = new MappingRegistry();
			registry.registerAction(TestAction.class, actionHandler);
			registry.registerInputKey(MidiInputKey.class);
			final MappingSerializer serializer = registry.build();

			final Mapping mapping = serializer.deserialize(IOUtils.readURL(MidiIntegrationTestMain.class.getClassLoader().getResource("pd12.json")));

			final Collection<MidiDeviceInfo> data = midi.getMidiDevices();
			for(final MidiDeviceInfo datum : data)
			{
				System.out.println(datum);
			}

			final MidiDevice device = midi.openDevice(new MidiDeviceInfo("PD 12", "PD 12", "Jammin Pro"), Midi.Mode.INPUT);
			device.getPublisher().addMidiListener(new MidiMappingListener(mapping, registry, registry));

			// Block until device is closed or Ctrl+C
			while(midi.isOpen())
			{
				Thread.sleep(200);
			}
		}
		catch(final Exception e)
		{
			e.printStackTrace();
		}
	}
}
