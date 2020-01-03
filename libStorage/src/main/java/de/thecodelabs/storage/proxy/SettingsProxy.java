package de.thecodelabs.storage.proxy;

import com.google.gson.Gson;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class SettingsProxy
{
	private static Map<Class<? extends Settings>, Settings> instances;
	static final Gson GSON = new Gson();

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
				settings.getClassLoader(),
				new Class[]{settings},
				new SettingsProxyHandler<>(settings)
		);
		instance.load();
		instance.init();

		instances.put(settings, instance);
		return (T) instance;
	}

	public static void saveAll()
	{
		instances.values().forEach(Settings::save);
	}
}
