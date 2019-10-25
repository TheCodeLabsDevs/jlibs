package de.thecodelabs.storage.proxy;

import com.google.gson.reflect.TypeToken;
import de.thecodelabs.storage.settings.Storage;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static de.thecodelabs.storage.proxy.SettingsProxy.GSON;

class SettingsProxyHandler<T extends Settings> implements InvocationHandler
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
			return executeGetPath(proxy, method, args);
		}

		else if(method.getName().equals("init"))
		{
			if(executeInit(proxy, method, args)) return null;
		}

		if(method.getName().equals("save"))
		{
			return executeSave((Settings) proxy);
		}
		else if(method.getName().equals("load"))
		{
			return executeLoad((Settings) proxy);
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

	private Object executeLoad(Settings proxy) throws IOException
	{
		final Path filePath = proxy.getPath();
		final boolean exists = Files.exists(filePath);

		if(exists)
		{
			Type type = new TypeToken<HashMap<String, Object>>(){}.getType();
			data = GSON.fromJson(Files.newBufferedReader(filePath), type);
		}

		if(data == null)
		{
			data = new HashMap<>();
		}
		return exists;
	}

	private Object executeGetPath(Object proxy, Method method, Object[] args) throws Throwable
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

			return result;
		}
		else
		{
			return Storage.getFilePath(settings);
		}
	}

	private Object executeSave(Settings proxy) throws IOException
	{
		final Path filePath = proxy.getPath();
		final String json = GSON.toJson(data);
		Files.write(filePath, json.getBytes());
		return null;
	}

	private boolean executeInit(Object proxy, Method method, Object[] args) throws Throwable
	{
		Constructor<MethodHandles.Lookup> constructor;
		Class<?> declaringClass;

		if(method.isDefault())
		{
			declaringClass = method.getDeclaringClass();
			constructor = MethodHandles.Lookup.class.getDeclaredConstructor(Class.class, int.class);
			constructor.setAccessible(true);
			constructor
					.newInstance(declaringClass, MethodHandles.Lookup.PRIVATE)
					.unreflectSpecial(method, declaringClass)
					.bindTo(proxy)
					.invokeWithArguments(args);

			return true;
		}
		return false;
	}
}
