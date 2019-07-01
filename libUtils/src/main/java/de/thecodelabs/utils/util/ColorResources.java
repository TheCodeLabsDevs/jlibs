package de.thecodelabs.utils.util;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class ColorResources
{
	private static final String COLORS_PROPERTIES = "colors.properties";

	private Properties properties;

	private static ColorResources instance;

	private Map<String, String> colors;

	private ColorResources()
	{
		properties = new Properties();
		try
		{
			properties.load(ColorResources.class.getClassLoader().getResourceAsStream(COLORS_PROPERTIES));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
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

	public String getColor(String key) {
		return properties.getProperty(key);
	}
}
