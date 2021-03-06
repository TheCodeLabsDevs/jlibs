package de.thecodelabs.storage.settings;

import de.thecodelabs.storage.settings.annotation.Classpath;
import de.thecodelabs.storage.settings.annotation.FilePath;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class Storage
{
	private static final String UTILS_PACKAGE = "de.thecodelabs.utils.application";

	private static Map<StorageType, StorageHandler> storageHandlerMap;

	static
	{
		storageHandlerMap = new HashMap<>();

		storageHandlerMap.put(StorageTypes.YAML, new YAMLHandler());
		storageHandlerMap.put(StorageTypes.JSON, new JsonHandler());
		storageHandlerMap.put(StorageTypes.PROPERTIES, new PropertiesHandler());
	}

	private Storage()
	{
	}

	public static <T> T load(InputStream stream, StorageType type, Class<T> clazz)
	{
		if(!storageHandlerMap.containsKey(type))
		{
			throw new IllegalArgumentException("Storage Type unsupported");
		}

		return storageHandlerMap.get(type).deserialize(stream, clazz);
	}

	public static <T> T load(Path path, StorageType type, Class<T> clazz)
	{
		if(!storageHandlerMap.containsKey(type))
		{
			throw new IllegalArgumentException("Storage Type unsupported");
		}

		StorageHandler handler = storageHandlerMap.get(type);
		try
		{
			if(Files.notExists(path))
			{
				Files.createDirectories(path.getParent());
				Files.createFile(path);

				System.out.println("Create new Config: " + path);
				T instance = clazz.newInstance();
				handler.serialize(path, instance);
				return instance;
			}

			return handler.deserialize(Files.newInputStream(path, StandardOpenOption.READ), clazz);
		}
		catch(IOException | InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> T load(StorageType type, Class<T> clazz)
	{
		InputStream inputStream = getInputStream(clazz);
		if(inputStream != null)
		{
			return load(inputStream, type, clazz);
		}
		else
		{
			return null;
		}
	}

	public static <T> void save(Path path, StorageType type, T value)
	{
		if(!storageHandlerMap.containsKey(type))
		{
			throw new IllegalArgumentException("Storage Type unsupported");
		}

		storageHandlerMap.get(type).serialize(path, value);
	}

	public static <T> void save(StorageType type, T value)
	{
		final Path path = getFilePath(value.getClass());

		if(path != null)
		{
			save(path, type, value);
		}
	}

	public static <T> InputStream getInputStream(Class<T> clazz)
	{
		if(clazz.isAnnotationPresent(FilePath.class))
		{
			try
			{
				return Files.newInputStream(getFilePath(clazz));
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		else if(clazz.isAnnotationPresent(Classpath.class))
		{
			return getClasspath(clazz);
		}
		else
		{
			throw new IllegalArgumentException("Class does not provide FilePath annotation");
		}
	}

	public static <T> Path getFilePath(Class<T> clazz)
	{
		try
		{
			final FilePath annotation = clazz.getAnnotation(FilePath.class);
			final String filePath = annotation.value();

			if(annotation.absolute())
			{
				return Paths.get(filePath);
			}

			Class<?> appClass = Class.forName(UTILS_PACKAGE + ".App");
			Class pathTypeClass = Class.forName(UTILS_PACKAGE + ".container.PathType");
			Class containerPathType = Class.forName(UTILS_PACKAGE + ".container.ContainerPathType");
			Class<?> appUtilsClass = Class.forName(UTILS_PACKAGE + ".ApplicationUtils");
			final Method getApplication = appUtilsClass.getMethod("getApplication");
			final Object app = getApplication.invoke(null);

			final Method getPath = appClass.getMethod("getPath", containerPathType, String[].class);
			return (Path) getPath.invoke(app, Enum.valueOf(pathTypeClass, "CONFIGURATION"), new String[]{filePath});

		}
		catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			throw new IllegalArgumentException("libUtils not exists");
		}
	}

	public static <T> InputStream getClasspath(Class<T> clazz)
	{
		final Classpath annotation = clazz.getAnnotation(Classpath.class);
		final String filePath = annotation.value();
		return clazz.getClassLoader().getResourceAsStream(filePath);
	}
}
