package de.thecodelabs.plugins;

import java.net.URLClassLoader;
import java.util.Arrays;

public class DuplicatedPluginException extends RuntimeException
{
	private final PluginDescriptor pluginDescriptor;

	public DuplicatedPluginException(PluginDescriptor pluginDescriptor, URLClassLoader urlClassLoader)
	{
		super("Plugin: " + pluginDescriptor.getName() + " is already loaded in class loader " + Arrays.toString(urlClassLoader.getURLs()));
		this.pluginDescriptor = pluginDescriptor;
	}

	public PluginDescriptor getPluginDescriptor()
	{
		return pluginDescriptor;
	}
}
