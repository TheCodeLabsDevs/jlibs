package de.thecodelabs.storage.proxy;

import java.nio.file.Path;

public interface Settings
{
	void init();

	void load();

	void save();

	Path getPath();
}
