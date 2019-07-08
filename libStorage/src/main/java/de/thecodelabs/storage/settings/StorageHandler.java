package de.thecodelabs.storage.settings;

import java.io.InputStream;
import java.nio.file.Path;

public interface StorageHandler
{
	<T> T deserialize(InputStream stream, Class<T> clazz);

	<T> void serialize(Path path, T t);
}
