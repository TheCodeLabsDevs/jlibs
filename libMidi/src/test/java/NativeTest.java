import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.MidiCommand;
import de.thecodelabs.midi.midi.MidiCommandHandler;
import de.thecodelabs.midi.midi.MidiCommandType;
import javafx.application.Application;
import javafx.stage.Stage;

public class NativeTest extends Application
{
	public static void main(String[] args)
	{
		try
		{
			MidiCommandHandler.getInstance().addMidiListener(i -> {
				System.out.println(i);
				Midi.getInstance().sendMessage(new MidiCommand(MidiCommandType.NOTE_ON, (byte) 0, i.getPayload()));
			});

			Midi.setUseNative(true);

			MidiDeviceInfo[] data = Midi.getInstance().getMidiDevices();
			for(MidiDeviceInfo datum : data)
			{
				System.out.println(datum);
			}

			Midi.getInstance().openDevice(new MidiDeviceInfo("CoreMIDI4J - PD 12", "PD 12", "Jammin Pro"), Midi.Mode.INPUT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{

	}
}
