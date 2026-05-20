import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.message.MidiMessage;
import de.thecodelabs.midi.midi.message.MidiMessageType;

public class NativeTest
{
	public static void main(final String[] args)
	{
		try
		{
			final Midi midi = Midi.getInstance();

			midi.getPublisher().addMidiListener(i -> {
				System.out.println(i);
				midi.sendMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, i.getPayload()));
			});

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
