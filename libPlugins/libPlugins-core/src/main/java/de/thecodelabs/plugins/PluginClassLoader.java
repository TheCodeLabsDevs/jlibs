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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

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

	@Override
	public Class<?> loadClass(String className) throws ClassNotFoundException
	{
		synchronized(getClassLoadingLock(className))
		{
			if(className.startsWith(JAVA_PACKAGE_PREFIX))
			{
				return findSystemClass(className);
			}

			if(className.startsWith("de.thecodelabs.plugins"))
			{
				return getParent().loadClass(className);
			}

			Class<?> loadedClass = findLoadedClass(className);
			if(loadedClass != null)
			{
				return loadedClass;
			}

			try
			{
				return super.loadClass(className);
			}
			catch(ClassCastException ignored)
			{
			}

			try
			{
				loadedClass = findClass(className);
				return loadedClass;
			}
			catch(ClassNotFoundException ignored)
			{
			}

			throw new ClassNotFoundException(className);
		}
	}

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

	public void loadPlugin(PluginManagerDelegate delegate)
	{
		URL resource = this.getResource("plugin.yml");
		if(resource == null)
		{
			resource = this.getResource("plugin.yaml");
			if(resource == null)
			{
				LoggerBridge.error("Missing plugin.yml in " + getURLs()[0]);
				return;
			}
		}

		try
		{
			pluginDescriptor = Storage.load(resource.openStream(), StorageTypes.YAML, PluginDescriptor.class);
			final Class<?> aClass = this.loadClass(pluginDescriptor.getMain());

			LoggerBridge.debug("Loading plugin: " + pluginDescriptor.getName() + ", version: " + pluginDescriptor.getVersion() + " (" + pluginDescriptor.getBuild() + ")");

			// Load plugin main class
			pluginInstance = (Plugin) aClass.getConstructor().newInstance();
			if(delegate != null)
			{
				delegate.pluginWillLoad(pluginInstance, pluginDescriptor);
			}
			pluginInstance.startup(pluginDescriptor);
			if(delegate != null)
			{
				delegate.pluginDidLoaded(pluginInstance, pluginDescriptor);
			}

			this.setLoaded(true);
		}
		catch(IOException | ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e)
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