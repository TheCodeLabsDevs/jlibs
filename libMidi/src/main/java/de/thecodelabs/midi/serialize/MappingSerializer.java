package de.thecodelabs.midi.serialize;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.thecodelabs.midi.Mapping;
import de.thecodelabs.midi.mapping.Key;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class MappingSerializer
{
	public static Mapping load(Path path) throws IOException
	{
		return load(Files.newBufferedReader(path));
	}

	public static Mapping load(InputStream inputStream)
	{
		final Gson gson = MappingSerializer.getSerializer();
		return gson.fromJson(new InputStreamReader(inputStream), Mapping.class);
	}

	public static Mapping load(Reader reader)
	{
		final Gson gson = MappingSerializer.getSerializer();
		return gson.fromJson(reader, Mapping.class);
	}

	public static void save(Mapping mapping, Path path) throws IOException
	{
		Files.write(path, serialize(mapping).getBytes());
	}

	public static void save(Mapping mapping, Appendable writer) throws IOException
	{
		writer.append(serialize(mapping));
	}

	private static String serialize(Mapping mapping)
	{
		final Gson gson = MappingSerializer.getSerializer();
		return gson.toJson(mapping);
	}

	private static Gson getSerializer()
	{
		return new GsonBuilder()
				.registerTypeAdapter(Key.class, new KeySerializer())
				.create();
	}
}
