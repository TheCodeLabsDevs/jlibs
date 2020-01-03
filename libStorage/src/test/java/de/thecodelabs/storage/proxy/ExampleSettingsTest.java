package de.thecodelabs.storage.proxy;

import java.nio.file.Path;

public class ExampleSettingsTest
{
	public static void main(String[] args)
	{
		final ExampleSettings settings = SettingsProxy.getSettings(ExampleSettings.class);
		final Path path = settings.getPath();
		System.out.println(path);
		settings.password("Neues Password");

		System.out.println(settings.password());
		settings.save();
	}
}
