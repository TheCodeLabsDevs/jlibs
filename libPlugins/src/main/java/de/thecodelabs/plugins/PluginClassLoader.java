/*
 * Copyright (C) 2012-present the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.thecodelabs.plugins;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.logger.LoggerBridge;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * One instance of this class should be created by plugin manager for every available plug-in.
 * By default, this class loader is a Parent Last ClassLoader - it loads the classes from the plugin's jars
 * before delegating to the parent class loader.
 *
 * @author Decebal Suiu
 */
public class PluginClassLoader extends URLClassLoader
{

	private static final String JAVA_PACKAGE_PREFIX = "java.";

	private boolean loaded;
	private PluginDescriptor pluginDescriptor;
	private Plugin pluginInstance;

	public PluginClassLoader(URL[] urls)
	{
		super(urls);
	}

	@Override
	public void addURL(URL url)
	{
		super.addURL(url);
	}

	public void addFile(File file)
	{
		try
		{
			addURL(file.getCanonicalFile().toURI().toURL());
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * By default, it uses a child first delegation model rather than the standard parent first.
	 * If the requested class cannot be found in this class loader, the parent class loader will be consulted
	 * via the standard {@link ClassLoader#loadClass(String)} mechanism.
	 */
	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException
	{
		synchronized(getClassLoadingLock(className))
		{
			// first check whether it's a system class, delegate to the system loader
			if(className.startsWith(JAVA_PACKAGE_PREFIX))
			{
				return findSystemClass(className);
			}

			// if the class is part of the plugin engine use parent class loader
			if(className.startsWith("de.thecodelabs.plugins"))
			{
				return getParent().loadClass(className);
			}

			// second check whether it's already been loaded
			Class<?> loadedClass = findLoadedClass(className);
			if(loadedClass != null)
			{
				return loadedClass;
			}

			// try to load from parent
			try
			{
				return super.loadClass(className);
			}
			catch(ClassCastException e)
			{
				// try next step
			}

			// nope, try to load locally
			try
			{
				loadedClass = findClass(className);
				return loadedClass;
			}
			catch(ClassNotFoundException e)
			{
				// try next step
			}

			throw new ClassNotFoundException(className);
		}
	}

	/**
	 * Load the named resource from this plugin.
	 * By default, this implementation checks the plugin's classpath first then delegates to the parent.
	 *
	 * @param name the name of the resource.
	 * @return the URL to the resource, {@code null} if the resource was not found.
	 */
	@Override
	public URL getResource(String name)
	{
		URL url = super.getResource(name);
		if(url != null)
		{
			return url;
		}

		return findResource(name);
	}

	public boolean isLoaded()
	{
		return loaded;
	}

	public void setLoaded(boolean loaded)
	{
		this.loaded = loaded;
	}

	public void loadPlugin()
	{
		final URL resource = this.getResource("plugin.yml");
		if(resource == null)
		{
			return;
		}
		try
		{
			pluginDescriptor = Storage.load(resource.openStream(), StorageTypes.YAML, PluginDescriptor.class);
			final Class<?> aClass = this.loadClass(pluginDescriptor.getMain());

			LoggerBridge.debug("Loading plugin: " + pluginDescriptor.getName() + ", version: " + pluginDescriptor.getVersion() + " (" + pluginDescriptor.getBuild() + ")");

			// Load plugin main class
			pluginInstance = (Plugin) aClass.newInstance();
			pluginInstance.startup();

			this.setLoaded(true);
		}
		catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Plugin getPluginInstance()
	{
		return pluginInstance;
	}

	public PluginDescriptor getPluginDescriptor()
	{
		return pluginDescriptor;
	}
}