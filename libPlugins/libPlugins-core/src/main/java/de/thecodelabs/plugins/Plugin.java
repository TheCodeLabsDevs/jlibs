package de.thecodelabs.plugins;

public interface Plugin
{
	void startup(PluginDescriptor descriptor);

	void shutdown();

	default PluginDescriptor getPluginDescriptor()
	{
		final ClassLoader classLoader = getClass().getClassLoader();
		if(classLoader instanceof PluginClassLoader)
			return ((PluginClassLoader) classLoader).getPluginDescriptor();
		return null;
	}
}
