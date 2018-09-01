import de.tobias.midi.Midi;

import javax.sound.midi.MidiUnavailableException;

public class MainTest
{
	public static void main(String[] args)
	{
		try
		{
			Midi.getInstance().lookupMidiDevice("MidiKeys", Midi.Mode.INPUT);

			System.out.println(Midi.getInstance().isModeSupported(Midi.Mode.INPUT));
			System.out.println(Midi.getInstance().isModeSupported(Midi.Mode.OUTPUT));
		}
		catch(MidiUnavailableException e)
		{
			e.printStackTrace();
		}
	}
}
