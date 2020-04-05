package de.thecodelabs.utils.ui.size;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.logger.LoggerBridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class NVCDatabase
{

	private static final Gson gson = new Gson();
	private static final String VIEW_STATE_PATH = "ViewState.json";

	private static HashMap<String, NVCItem> items;

	static
	{
		items = new HashMap<>();
	}

	public static NVCItem getItem(Class<?> clazz)
	{
		String name = clazz.getName();
		if(!items.containsKey(name))
		{
			items.put(name, new NVCItem());
		}
		return items.get(name);
	}

	private static boolean isLoaded = false;

	public static void load()
	{
		if(isLoaded)
		{
			return;
		}

		isLoaded = true;

		App app = ApplicationUtils.getApplication();
		if(app != null)
		{
			Path path = app.getPath(PathType.CONFIGURATION, VIEW_STATE_PATH);
			if(Files.exists(path))
			{
				try (final BufferedReader reader = Files.newBufferedReader(path))
				{
					gson.fromJson(reader, new TypeToken<Map<String, NVCItem>>() {}.getType());
				}
				catch(IOException e)
				{
					LoggerBridge.error(e);
				}
			}
		}
	}

	public static void save()
	{
		App app = ApplicationUtils.getApplication();
		if(app != null)
		{
			Path path = app.getPath(PathType.CONFIGURATION, VIEW_STATE_PATH);

			if(Files.notExists(path))
			{
				try
				{
					Files.createDirectories(path.getParent());
					Files.createFile(path);
					return;
				}
				catch(IOException e)
				{
					LoggerBridge.error(e);
				}
			}

			final String json = gson.toJson(items);
			try
			{
				Files.write(path, json.getBytes());
			}
			catch(Exception e)
			{
				LoggerBridge.error(e);
			}
		}
	}
}
