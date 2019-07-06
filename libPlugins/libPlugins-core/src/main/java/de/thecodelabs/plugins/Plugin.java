package de.thecodelabs.plugins;

public interface Plugin
{
	void startup(PluginDescriptor descriptor);

	void shutdown();
}
