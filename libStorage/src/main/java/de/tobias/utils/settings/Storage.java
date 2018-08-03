package de.tobias.utils.settings;

import de.tobias.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

public class Storage {

	private static Map<StorageType, StorageHandler> storageHandlerMap;

	static {
		storageHandlerMap = new HashMap<>();

		storageHandlerMap.put(StorageTyps.YAML, new YAMLHandler());
		storageHandlerMap.put(StorageTyps.PROPERTIES, new PropertiesHandler());
	}

	public static <T> T load(InputStream stream, StorageType type, Class<T> clazz) {
		if (!storageHandlerMap.containsKey(type)) {
			throw new IllegalArgumentException("Storage Type unsupported");
		}

		return storageHandlerMap.get(type).deserialize(stream, clazz);
	}

	public static <T> T load(Path path, StorageType type, Class<T> clazz) {
		if (!storageHandlerMap.containsKey(type)) {
			throw new IllegalArgumentException("Storage Type unsupported");
		}

		StorageHandler handler = storageHandlerMap.get(type);
		try {
			if (Files.notExists(path)) {
				Files.createDirectories(path.getParent());
				Files.createFile(path);

				Logger.debug("Create new Config: ", path);
				T instance = clazz.newInstance();
				handler.serialize(path, instance);
				return instance;
			}

			return handler.deserialize(Files.newInputStream(path, StandardOpenOption.READ), clazz);
		} catch (IOException | InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void save(Path path, StorageType type, T value) {
		if (!storageHandlerMap.containsKey(type)) {
			throw new IllegalArgumentException("Storage Type unsupported");
		}

		storageHandlerMap.get(type).serialize(path, value);
	}
}
