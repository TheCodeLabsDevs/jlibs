import com.google.gson.Gson;
import de.tobias.midi.Mapping;
import de.tobias.midi.Midi;
import de.tobias.midi.action.ActionRegistry;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.serialize.MappingSerializer;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MainTest
{
	public static void main(String[] args)
	{
		try
		{
			ActionRegistry.registerActionHandler(new EimerActionHandler());

			Gson gson = MappingSerializer.getSerializer();
			Mapping mapping = gson.fromJson(Files.newBufferedReader(Paths.get("midi.json")), Mapping.class);

			Mapping.setCurrentMapping(mapping);

			Midi.setUseNative(false);
			Midi.getInstance().openDevice(new MidiDeviceInfo("PD 12", "PD 12"), Midi.Mode.INPUT, Midi.Mode.OUTPUT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
