package de.thecodelabs.utils.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public class ColorResources
{
	private static final String COLORS_PROPERTIES = "colors.properties";

	private final Properties properties;

	private static ColorResources instance;

	private ColorResources()
	{
		properties = new Properties();
		try
		{
			properties.load(ColorResources.class.getClassLoader().getResourceAsStream(COLORS_PROPERTIES));
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public static ColorResources getInstance()
	{
		if(instance == null)
		{
			instance = new ColorResources();
		}
		return instance;
	}

	public String getColor(String key)
	{
		return properties.getProperty(key);
	}
}
