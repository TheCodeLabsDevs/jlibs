package de.thecodelabs.utils.util;

import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.localization.LocalizationMessageFormatter;
import de.thecodelabs.utils.util.localization.MultipleResourceBundle;
import de.thecodelabs.utils.util.localization.formatter.CustomMessageFormatter;

import java.util.*;

public class Localization
{

	private static LocalizationDelegate delegate;
	private static LocalizationMessageFormatter formatter;

	public static void setDelegate(LocalizationDelegate delegate)
	{
		Localization.delegate = delegate;
	}

	private static MultipleResourceBundle bundle;

	public static void load()
	{
		if(delegate == null)
		{
			throw new NullPointerException("Delegate is null. Use: Localization.setDelegate()");
		}
		List<ResourceBundle> bundles = new ArrayList<>();

		if(delegate.useMultipleResourceBundles())
		{
			for(String bundle : delegate.getBaseResources())
			{
				bundles.add(loadResourceBundle(bundle));
			}
		}
		else
		{
			final String baseResource = delegate.getBaseResource();
			if(baseResource == null)
			{
				LoggerBridge.debug("Resource bundle is null. Delegate Method might be not overwritten");
				return;
			}

			bundles.add(loadResourceBundle(baseResource));
		}
		Localization.bundle = new MultipleResourceBundle(bundles, delegate.getLocale());
		Localization.formatter = delegate.messageFormatter();
	}

	public static void addResourceBundle(String name, ClassLoader loader) {
		addResourceBundle(loadBundle(name, loader));
	}

	public static void addResourceBundle(ResourceBundle resourceBundle)
	{
		bundle = new MultipleResourceBundle(bundle, resourceBundle);
		LoggerBridge.debug("Clear ResourceBundle Cache");
		ResourceBundle.clearCache(Localization.class.getClassLoader());
	}

	private static ResourceBundle loadResourceBundle(String base)
	{
		ResourceBundle bundle = loadBundle(base, Localization.class.getClassLoader());
		LoggerBridge.debug("Loaded localization bundle: " + bundle.getBaseBundleName() + " for language: " + bundle.getLocale());
		return bundle;
	}

	public static ResourceBundle loadBundle(String name, ClassLoader loader)
	{
		Locale locale = delegate == null ? Locale.ENGLISH : delegate.getLocale();
		try
		{
			return ResourceBundle.getBundle(name, locale, loader);
		}
		catch(MissingResourceException e)
		{
			return ResourceBundle.getBundle(name, Locale.ENGLISH, loader);
		}
	}

	public static ResourceBundle getBundle()
	{
		return bundle;
	}

	/**
	 * Gets the localized message for the given key or returns the key itself if the key is not existing in any bundle.
	 */
	private static String getRawString(String key)
	{
		if(bundle.containsKey(key))
		{
			return bundle.getString(key);
		}
		else
		{
			LoggerBridge.debug("ResourceKey not found: " + key);
			return key;
		}
	}

	public static String getString(String key, Object... args)
	{
		final String message = getRawString(key);
		return formatter.format(message, args);
	}

	public interface LocalizationDelegate
	{
		Locale getLocale();

		default String getBaseResource()
		{
			return null;
		}

		default String[] getBaseResources()
		{
			return new String[]{};
		}

		default boolean useMultipleResourceBundles()
		{
			return false;
		}

		default LocalizationMessageFormatter messageFormatter()
		{
			return new CustomMessageFormatter();
		}
	}
}
