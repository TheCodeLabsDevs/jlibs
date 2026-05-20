import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.MidiInputPublisher;
import de.thecodelabs.midi.midi.MidiMessage;
import de.thecodelabs.midi.midi.MidiMessageType;

public class NativeTest
{
	public static void main(final String[] args)
	{
		try
		{
			MidiInputPublisher.getInstance().addMidiListener(i -> {
				System.out.println(i);
				Midi.getInstance().sendMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, i.getPayload()));
			});

			final MidiDeviceInfo[] data = Midi.getInstance().getMidiDevices();
			for(final MidiDeviceInfo datum : data)
			{
				System.out.println(datum);
			}

			Midi.getInstance().openDevice(new MidiDeviceInfo("PD 12", "PD 12", "Jammin Pro"), Midi.Mode.INPUT);

			// Block until device is closed or Ctrl+C
			while(Midi.getInstance().isOpen())
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
