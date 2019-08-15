package de.thecodelabs.storage.proxy;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import de.thecodelabs.storage.settings.Storage;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class SettingsProxy
{
	private static Map<Class<? extends Settings>, Settings> instances;
	private static final Gson GSON = new Gson();

	private static class SettingsProxyHandler<T extends Settings> implements InvocationHandler
	{
		private Map<String, Object> data = new HashMap<>();

		private Class<T> settings;

		public SettingsProxyHandler(Class<T> settings)
		{
			this.settings = settings;
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
		{
			if(method.getName().equals("toString"))
			{
				return "SettingsProxy$" + proxy.getClass();
			}
			else if(method.getName().equals("getPath"))
			{
				Constructor<MethodHandles.Lookup> constructor;
				Class<?> declaringClass;
				Object result;

				if(method.isDefault())
				{
					declaringClass = method.getDeclaringClass();
					constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
					constructor.setAccessible(true);
					result = constructor
							.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
							.unreflectSpecial(method, declaringClass)
							.bindTo(proxy)
							.invokeWithArguments(args);

					return (result);
				}
				else
				{
					return Storage.getFilePath(settings);
				}
			}

			if(method.getName().equals("save"))
			{
				final Path filePath = ((Settings) proxy).getPath();
				final String json = GSON.toJson(data);
				Files.write(filePath, json.getBytes());
				return null;
			}
			else if(method.getName().equals("load"))
			{
				final Path filePath = ((Settings) proxy).getPath();
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

			Object value = data.get(method.getName());
			if(value == null)
			{
				if(method.isAnnotationPresent(DefaultString.class))
				{
					value = method.getAnnotation(DefaultString.class).value();
					data.put(method.getName(), value);
				}

				if(method.isAnnotationPresent(DefaultBoolean.class))
				{
					value = method.getAnnotation(DefaultBoolean.class).value();
					data.put(method.getName(), value);
				}

				if(method.isAnnotationPresent(DefaultInteger.class))
				{
					value = method.getAnnotation(DefaultInteger.class).value();
					data.put(method.getName(), value);
				}

				if(method.isAnnotationPresent(DefaultDouble.class))
				{
					value = method.getAnnotation(DefaultDouble.class).value();
					data.put(method.getName(), value);
				}
			}
			return value;
		}
	}

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
				new SettingsProxyHandler<>(settings)
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
