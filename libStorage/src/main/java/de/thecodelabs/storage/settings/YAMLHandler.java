package de.thecodelabs.storage.settings;

import de.thecodelabs.storage.settings.annotation.Key;
import de.thecodelabs.storage.settings.annotation.Required;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

class YAMLHandler implements StorageHandler
{

	@Override
	public <T> T deserialize(InputStream stream, Class<T> clazz)
	{
		try
		{
			Yaml yaml = new Yaml();
			Map<String, Object> values = yaml.load(stream);

			T serializable = clazz.newInstance();

			for(Field field : clazz.getDeclaredFields())
			{
				field.setAccessible(true);
				if(field.isAnnotationPresent(Key.class))
				{
					if(!Modifier.isFinal(field.getModifiers()))
					{
						Key key = field.getAnnotation(Key.class);
						final String name = key.value().isEmpty() ? field.getName() : key.value();

						String[] nameComponents = name.contains(".") ? name.split("\\.") : new String[]{name};
						Map<String, Object> depthMap = values;
						boolean found = false;
						for(String component : nameComponents)
						{
							if(depthMap.containsKey(component))
							{
								if(depthMap.get(component) instanceof Map<?, ?> && !field.getType().equals(Map.class))
								{
									//noinspection unchecked
									depthMap = (Map<String, Object>) depthMap.get(component);
								}
								else
								{
									field.set(serializable, depthMap.get(component));
									found = true;
								}
							}
						}
						if(!found && field.isAnnotationPresent(Required.class))
						{
							throw new RequiredAttributeException(field);
						}
					}
				}
			}
			return serializable;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> void serialize(Path path, T t)
	{
		try
		{
			if(Files.notExists(path))
			{
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
			Yaml yaml = new Yaml();
			Map<String, Object> values = yaml.load(Files.newInputStream(path, StandardOpenOption.READ));

			if (values == null) {
				values = new HashMap<>();
			}

			for(Field field : t.getClass().getDeclaredFields())
			{
				field.setAccessible(true);
				if(field.isAnnotationPresent(Key.class))
				{
					Key key = field.getAnnotation(Key.class);
					final String name = key.value().isEmpty() ? field.getName() : key.value();

					values.put(name, field.get(t));
				}
			}
			yaml.dump(values, Files.newBufferedWriter(path));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
