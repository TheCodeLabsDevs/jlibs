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
import de.thecodelabs.midi.midi.message.MidiMessage;
import de.thecodelabs.midi.midi.message.MidiMessageType;
import de.thecodelabs.utils.io.IOUtils;

import java.util.Collection;

public class LaunchPadMidiIntegrationTestMain
{
	static void main()
	{
		try(final Midi midi = new Midi())
		{
			final TestActionHandler actionHandler = new TestActionHandler();

			final MappingRegistry registry = new MappingRegistry();
			registry.registerAction(TestAction.class, actionHandler);
			registry.registerInputKey(MidiInputKey.class);
			final MappingSerializer serializer = registry.build();

			final Mapping mapping = serializer.deserialize(IOUtils.readURL(LaunchPadMidiIntegrationTestMain.class.getClassLoader().getResource("pd12.json")));

			final Collection<MidiDeviceInfo> data = midi.getMidiDevices();
			for(final MidiDeviceInfo datum : data)
			{
				System.out.println(datum);
			}

			midi.addMidiListener(device -> {
				device.sendMidiMessage(new MidiMessage(new byte[]{(byte) 240, 126, 127, 6, 1, (byte) 247}));
				device.sendMidiMessage(new MidiMessage(new byte[]{(byte) 240, 0, 32, 41, 2, 13, 0, 127, (byte) 247}));
				device.sendMidiMessage(new MidiMessage(new byte[]{(byte) 240, 0, 32, 41, 2, 13, 14, 1, (byte) 247}));

			});

			final MidiDevice device = midi.openDevice(new MidiDeviceInfo("LPMiniMK3 MIDI Out", "LPMiniMK3 MIDI", "Focusrite - Novation"), Midi.Mode.INPUT, Midi.Mode.OUTPUT);
			device.getPublisher().addMidiListener(new MidiMappingListener(mapping, registry));

			device.sendMidiMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, (byte) 81, (byte) 3));
			Thread.sleep(2000);
			device.sendMidiMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 2, (byte) 81, (byte) 5));
			Thread.sleep(2000);
			device.sendMidiMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, (byte) 81, (byte) 3));
			device.sendMidiMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 1, (byte) 81, (byte) 5));
			Thread.sleep(2000);
			device.sendMidiMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, (byte) 81, (byte) 0));

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
