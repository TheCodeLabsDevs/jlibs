package de.thecodelabs.plugins;

public interface PluginManagerDelegate
{
	void pluginWillLoad(Plugin plugin, PluginDescriptor pluginDescriptor);

	void pluginDidLoaded(Plugin plugin, PluginDescriptor pluginDescriptor);
}
