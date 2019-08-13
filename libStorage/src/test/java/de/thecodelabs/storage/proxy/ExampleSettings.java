package de.thecodelabs.storage.proxy;

import de.thecodelabs.storage.settings.annotation.FilePath;

@FilePath(value = "/Users/tobias/Documents/Programmieren/Projects/jlibs/libStorage/Proxy.json", absolute = true)
public interface ExampleSettings extends Settings
{
	String password();

	@Setter
	void password(String password);
}
