package de.thecodelabs.midi.serialize;

import com.google.gson.Gson;
import de.thecodelabs.midi.MappingCollection;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;

public class MappingCollectionSerializer
{
	public static MappingCollection load(Path path) throws IOException
	{
		return load(Files.newBufferedReader(path));
	}

	public static MappingCollection load(InputStream inputStream)
	{
		final Gson gson = MappingSerializer.getSerializer();
		return gson.fromJson(new InputStreamReader(inputStream), MappingCollection.class);
	}

	public static MappingCollection load(Reader reader)
	{
		final Gson gson = MappingSerializer.getSerializer();
		return gson.fromJson(reader, MappingCollection.class);
	}

	public static void save(MappingCollection mapping, Path path) throws IOException
	{
		Files.write(path, serialize(mapping).getBytes());
	}

	public static void save(MappingCollection mapping, Appendable writer) throws IOException
	{
		writer.append(serialize(mapping));
	}

	private static String serialize(MappingCollection mapping)
	{
		final Gson gson = MappingSerializer.getSerializer();
		return gson.toJson(mapping);
	}
}
