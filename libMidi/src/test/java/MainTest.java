import com.google.gson.Gson;
import de.thecodelabs.midi.Mapping;
import de.thecodelabs.midi.Midi;
import de.thecodelabs.midi.action.ActionRegistry;
import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.serialize.MappingSerializer;

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

			Midi.setUseNative(true);
			Mapping.setCurrentMapping(mapping);

			Midi.getInstance().openDevice(new MidiDeviceInfo("PD 12", "PD 12", ""), Midi.Mode.INPUT, Midi.Mode.OUTPUT);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
