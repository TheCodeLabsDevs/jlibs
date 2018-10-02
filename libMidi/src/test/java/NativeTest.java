import de.tobias.midi.Midi;
import de.tobias.midi.MidiCommand;
import de.tobias.midi.MidiCommandHandler;
import de.tobias.midi.MidiCommandType;
import de.tobias.midi.device.MidiDeviceInfo;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Arrays;

public class NativeTest extends Application
{
	public static void main(String[] args)
	{
		System.load("/Users/tobias/Documents/Programmieren/Projects/nativeLibs/libMidi/Build/Products/Debug/liblibMidi.dylib");
		try
		{
			MidiCommandHandler.getInstance().addMidiListener(i -> {
				System.out.println(i);
				Midi.getInstance().sendMessage(new MidiCommand(MidiCommandType.NOTE_ON, (byte) 0, i.getPayload()));
			});
			Midi.setUseNative(false);

			MidiDeviceInfo[] data = Midi.getInstance().getMidiDevices();
			System.out.println(Arrays.toString(data));

			Midi.getInstance().openDevice(new MidiDeviceInfo("PD 12", "PD 12", "Jammin Pro"), Midi.Mode.INPUT);
//			System.out.println(Midi.getInstance().getDevice());

			//Midi.getInstance().close();
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
