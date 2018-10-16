package de.thecodelabs.storage.settings;

import de.thecodelabs.storage.settings.annotation.Key;
import de.thecodelabs.storage.settings.annotation.Required;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

class PropertiesHandler implements StorageHandler {

	@Override
	public <T> T deserialize(InputStream stream, Class<T> clazz) {
		try {
			Properties properties = new Properties();
			properties.load(stream);

			T serializable = clazz.newInstance();

			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Key.class)) {
					if (!Modifier.isFinal(field.getModifiers())) {
						Key key = field.getAnnotation(Key.class);
						final String name = key.value().isEmpty() ? field.getName() : key.value();
						if (properties.containsKey(name)) {
							field.set(serializable, properties.getProperty(name));
						} else if (field.isAnnotationPresent(Required.class)) {
							throw new RequiredAttributeException(field);
						}
					}
				}
			}
			return serializable;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> void serialize(Path path, T t) {
		try {
			if (Files.notExists(path)) {
				Files.createDirectories(path.getParent());
				Files.createFile(path);
			}
			Properties properties = new Properties();

			for (Field field : t.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Key.class)) {
					Key key = field.getAnnotation(Key.class);
					final String name = key.value().isEmpty() ? field.getName() : key.value();

					properties.setProperty(name, field.get(t).toString());
				}
			}
			properties.store(Files.newOutputStream(path, StandardOpenOption.WRITE), "Settings");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
