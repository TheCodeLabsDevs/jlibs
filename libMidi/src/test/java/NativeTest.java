import de.tobias.midi.Midi;
import de.tobias.midi.MidiCommand;
import de.tobias.midi.MidiCommandHandler;
import de.tobias.midi.MidiCommandType;
import de.tobias.midi.device.MidiDeviceInfo;
import javafx.application.Application;
import javafx.stage.Stage;

import java.lang.reflect.Method;

public class NativeTest extends Application
{
	public static void main(String[] args)
	{
		try
		{
			final Class<?> aClass = Class.forName("uk.co.xfactorylibrarians.coremidi4j.Loader");
			final Method locateLibrary = aClass.getDeclaredMethod("locateLibrary");
			locateLibrary.setAccessible(true);
			System.out.println(locateLibrary.invoke(null));

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

			Midi.getInstance().openDevice(new MidiDeviceInfo("PD 12", "PD 12", "Jammin Pro"), Midi.Mode.INPUT);
			System.out.println(Midi.getInstance().getDevice());
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
