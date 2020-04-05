package de.thecodelabs.storage.settings;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class UserDefaults
{
	private static final Gson gson = new Gson();

	private Map<String, Object> data = new HashMap<>();

	private UserDefaults()
	{
	}

	public Object getData(String key)
	{
		return data.get(key);
	}

	public void setData(String key, Object data)
	{
		this.data.put(key, data);
	}

	public void clearData(String key)
	{
		data.remove(key);
	}

	public void clear()
	{
		data.clear();
	}

	public static UserDefaults load(Path path) throws IOException
	{
		UserDefaults defaults;

		if(Files.exists(path))
		{
			defaults = gson.fromJson(Files.newBufferedReader(path), UserDefaults.class);
		}
		else
		{
			defaults = new UserDefaults();
		}

		return defaults;
	}

	public void save(Path path) throws IOException
	{
		if(Files.notExists(path))
		{
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}

		final String json = gson.toJson(this);
		Files.write(path, json.getBytes());
	}
}
