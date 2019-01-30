package de.thecodelabs.plugins;

import de.thecodelabs.utils.io.PathUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

	public void addFolder(Path path)
	{
		try
		{
			Files.newDirectoryStream(path).forEach(file -> {
				if(PathUtils.getFileExtension(file).equalsIgnoreCase("jar"))
				{
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

	public List<Plugin> getPlugins()
	{
		return pluginClassLoaders.stream().map(PluginClassLoader::getPluginInstance).collect(Collectors.toList());
	}

	public PluginDescriptor getPluginDescriptor(Plugin plugin)
	{
		final Optional<PluginClassLoader> loaderOptional = pluginClassLoaders.stream().filter(loader -> loader.getPluginInstance() == plugin).findFirst();
		return loaderOptional.map(PluginClassLoader::getPluginDescriptor).orElse(null);
	}

	public void loadPlugins()
	{
		for(PluginClassLoader pluginClassLoader : pluginClassLoaders)
		{
			if(!pluginClassLoader.isLoaded())
			{
				pluginClassLoader.loadPlugin();
			}
		}
	}

	public void shutdown()
	{
		for(PluginClassLoader pluginClassLoader : pluginClassLoaders)
		{
			if(pluginClassLoader.isLoaded())
			{
				pluginClassLoader.getPluginInstance().shutdown();
			}
		}
		pluginClassLoaders.clear();
	}
}
