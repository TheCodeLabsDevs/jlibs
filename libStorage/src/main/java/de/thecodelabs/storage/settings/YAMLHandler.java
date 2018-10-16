package de.thecodelabs.storage.settings;

import de.thecodelabs.storage.settings.annotation.Key;
import de.thecodelabs.storage.settings.annotation.Required;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

class YAMLHandler implements StorageHandler {

	@Override
	public <T> T deserialize(InputStream stream, Class<T> clazz) {
		try {
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(stream);
			T serializable = clazz.newInstance();

			for (Field field : clazz.getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Key.class)) {
					if (!Modifier.isFinal(field.getModifiers())) {
						Key key = field.getAnnotation(Key.class);
						final String name = key.value().isEmpty() ? field.getName() : key.value();
						if (cfg.isSet(name)) {
							field.set(serializable, cfg.get(name));
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
			FileConfiguration cfg = YamlConfiguration.loadConfiguration(Files.newInputStream(path, StandardOpenOption.READ));

			for (Field field : t.getClass().getDeclaredFields()) {
				field.setAccessible(true);
				if (field.isAnnotationPresent(Key.class)) {
					Key key = field.getAnnotation(Key.class);
					final String name = key.value().isEmpty() ? field.getName() : key.value();

					cfg.set(name, field.get(t));
				}
			}
			cfg.save(path.toString());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
