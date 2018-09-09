package de.tobias.midi.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tobias.midi.mapping.Key;

public class MappingSerializer
{

	public static Gson getSerializer()
	{
		return new GsonBuilder()
				.registerTypeAdapter(Key.class, new KeySerializer())
				.create();
	}
}
