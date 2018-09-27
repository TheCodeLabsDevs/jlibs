import de.tobias.midi.Midi;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.event.KeyEventDispatcher;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.Arrays;

public class NativeTest extends Application
{
	public static void main(String[] args)
	{
		System.load("/Users/tobias/Documents/Programmieren/Projects/nativeLibs/libMidi/Build/Products/Debug/liblibMidi.dylib");
		MidiDeviceInfo[] data = Midi.getInstance().getMidiDevices();
		try
		{
			KeyEventDispatcher.registerKeyEventHandler(keyEvent -> System.out.println(keyEvent.getKeyValue()));
			Midi.getInstance().openInputDevice(data[2]);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println(Arrays.toString(data));
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{

	}
}
