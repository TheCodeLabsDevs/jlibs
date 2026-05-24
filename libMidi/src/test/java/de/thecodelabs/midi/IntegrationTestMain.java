package de.thecodelabs.midi;

import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.MappingRegistry;
import de.thecodelabs.midi.mapping.MappingSerializer;
import de.thecodelabs.midi.mapping.action.TestAction;
import de.thecodelabs.midi.mapping.action.TestActionHandler;
import de.thecodelabs.midi.mapping.input.MidiInputKey;
import de.thecodelabs.midi.mapping.listener.MidiMappingListener;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.utils.io.IOUtils;

public class IntegrationTestMain
{
	public static void main(final String[] args)
	{
		try
		{
			final TestActionHandler actionHandler = new TestActionHandler();

			final MappingRegistry registry = new MappingRegistry();
			registry.registerAction(TestAction.class, actionHandler);
			registry.registerInputKey(MidiInputKey.class);
			final MappingSerializer serializer = registry.build();

			final Mapping mapping = serializer.deserialize(IOUtils.readURL(IntegrationTestMain.class.getClassLoader().getResource("pd12.json")));

			final Midi midi = Midi.getInstance();
			midi.getPublisher().addMidiListener(new MidiMappingListener(mapping, registry));

			final MidiDeviceInfo[] data = midi.getMidiDevices();
			for(final MidiDeviceInfo datum : data)
			{
				System.out.println(datum);
			}

			midi.openDevice(new MidiDeviceInfo("PD 12", "PD 12", "Jammin Pro"), Midi.Mode.INPUT);

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
