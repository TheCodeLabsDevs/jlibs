package de.thecodelabs.storage.proxy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.thecodelabs.storage.settings.Storage;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SettingsProxy
{
	private static Map<Class<? extends Settings>, Settings> instances;
	private static final Gson GSON = new Gson();

	static
	{
		instances = new HashMap<>();
	}

	private SettingsProxy()
	{
	}

	@SuppressWarnings("unchecked")
	public static <T extends Settings> T getSettings(Class<T> settings)
	{
		if(instances.containsKey(settings))
		{
			return (T) instances.get(settings);
		}

		final Settings instance = (Settings) Proxy.newProxyInstance(
				SettingsProxy.class.getClassLoader(),
				new Class[]{settings},
				new InvocationHandler()
				{
					private Map<String, Object> data = new HashMap<>();

					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
					{
						if(method.getName().equals("save"))
						{
							final Path filePath = Storage.getFilePath(settings);
							final String json = GSON.toJson(data);
							Files.write(filePath, json.getBytes());
							return null;
						}
						else if(method.getName().equals("load"))
						{
							final Path filePath = Storage.getFilePath(settings);
							if(Files.exists(filePath))
							{
								Type type = new TypeToken<HashMap<String, Object>>()
								{
								}.getType();
								data = GSON.fromJson(Files.newBufferedReader(filePath), type);
							}

							if(data == null)
							{
								data = new HashMap<>();
							}
							return null;
						}

						if(method.isAnnotationPresent(Setter.class))
						{
							data.put(method.getName(), args[0]);
							return null;
						}

						return data.get(method.getName());
					}
				}
		);
		instance.load();

		instances.put(settings, instance);
		return (T) instance;
	}

	public static void saveAll()
	{
		instances.values().forEach(Settings::save);
	}
}
