package de.thecodelabs.storage.proxy;

public class ExampleSettingsTest
{
	public static void main(String[] args)
	{
		final ExampleSettings settings = SettingsProxy.getSettings(ExampleSettings.class);
		settings.password("Neues Password");

		System.out.println(settings.password());
		settings.save();
	}
}
