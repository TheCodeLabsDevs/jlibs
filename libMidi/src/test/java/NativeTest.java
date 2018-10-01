import de.tobias.midi.Midi;
import de.tobias.midi.MidiCommandHandler;
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
			MidiCommandHandler.getInstance().addMidiListener(System.out::println);
			Midi.setUseNative(true);

			MidiDeviceInfo[] data = Midi.getInstance().getMidiDevices();
			System.out.println(Arrays.toString(data));

			Midi.getInstance().openInputDevice(data[2]);

			System.out.println(Midi.getInstance().getDevice(Midi.Mode.INPUT));
			System.out.println(Midi.getInstance().isModeSupported(Midi.Mode.INPUT));
			System.out.println(Midi.getInstance().isModeSupported(Midi.Mode.OUTPUT));
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
