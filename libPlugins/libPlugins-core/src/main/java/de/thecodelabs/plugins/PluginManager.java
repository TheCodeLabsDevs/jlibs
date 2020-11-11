package de.thecodelabs.plugins;

import de.thecodelabs.utils.io.PathUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PluginManager
{
	private static final String JAR = "jar";
	private static PluginManager instance;

	public static PluginManager getInstance()
	{
		if(instance == null)
		{
			instance = new PluginManager();
		}
		return instance;
	}

	private final List<PluginClassLoader> pluginClassLoaders;

	private PluginManager()
	{
		this.pluginClassLoaders = new ArrayList<>();
	}

	/**
	 * Add all jars of a directory to the plugin manager.
	 *
	 * @param path directory
	 */
	public void addPluginsOfDirectory(Path path)
	{
		try(DirectoryStream<Path> stream = Files.newDirectoryStream(path))
		{
			stream.forEach(file -> {
				if(PathUtils.getFileExtension(file).equalsIgnoreCase(JAR))
				{
					addPluginFile(file);
				}
			});
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Add a jar plugin to the system (or a folder containing all class files)
	 *
	 * @param path path to plugin
	 */
	public void addPluginFile(Path path)
	{
		try
		{
			URL url = path.toUri().toURL();
			pluginClassLoaders.add(new PluginClassLoader(new URL[]{url}));
		}
		catch(MalformedURLException e)
		{
			throw new UncheckedIOException(e);
		}
	}

	public List<Plugin> getLoadedPlugins()
	{
		return pluginClassLoaders.stream()
				.filter(PluginClassLoader::isLoaded)
				.map(PluginClassLoader::getPluginInstance)
				.collect(Collectors.toList());
	}

	public Optional<PluginDescriptor> getPluginDescriptor(Plugin plugin)
	{
		final Optional<PluginClassLoader> loaderOptional = pluginClassLoaders.stream()
				.filter(loader -> loader.getPluginInstance() == plugin)
				.findFirst();
		return loaderOptional.map(PluginClassLoader::getPluginDescriptor);
	}

	public Class<?> loadClass(String name) throws ClassNotFoundException
	{
		for(PluginClassLoader pluginClassLoader : pluginClassLoaders)
		{
			try
			{
				return pluginClassLoader.loadClass(name);
			}
			catch(ClassNotFoundException ignored)
			{
			}
		}
		return Class.forName(name);
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
