package de.thecodelabs.plugins;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.io.PathUtils;
import de.thecodelabs.utils.logger.LoggerBridge;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class PluginManager
{

	private static PluginManager mInstance;

	public static PluginManager getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new PluginManager();
		}
		return mInstance;
	}

	private PluginManager()
	{
		this.pluginClassLoaders = new ArrayList<>();
	}

	private List<PluginClassLoader> pluginClassLoaders;

	public void addFolder(Path path) {
		try
		{
			Files.newDirectoryStream(path).forEach(file -> {
				if (PathUtils.getFileExtension(file).equalsIgnoreCase("jar")) {
					addFile(file);
				}
			});
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void addFile(Path path)
	{
		try
		{
			URL url = path.toUri().toURL();
			pluginClassLoaders.add(new PluginClassLoader(new URL[]{url}));
		}
		catch(MalformedURLException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void loadPlugins()
	{
		for(PluginClassLoader pluginClassLoader : pluginClassLoaders)
		{
			if(!pluginClassLoader.isLoaded())
			{
				loadPlugin(pluginClassLoader);
			}
		}
	}

	private void loadPlugin(PluginClassLoader pluginClassLoader)
	{
		final URL resource = pluginClassLoader.getResource("plugin.yml");
		if(resource == null)
		{
			return;
		}
		try
		{
			final PluginDescriptor pluginDescriptor = Storage.load(resource.openStream(), StorageTypes.YAML, PluginDescriptor.class);
			final Class<?> aClass = pluginClassLoader.loadClass(pluginDescriptor.getMain());

			LoggerBridge.debug("Loading plugin: " + pluginDescriptor.getName() + ", version: " + pluginDescriptor.getVersion() + " (" + pluginDescriptor.getBuild() + ")");

			// Load plugin main class
			Plugin plugin = (Plugin) aClass.newInstance();
			plugin.startup();

			pluginClassLoader.setLoaded(true);
		}
		catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}
}
