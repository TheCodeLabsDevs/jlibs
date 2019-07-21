package de.thecodelabs.utils.util;

import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.localization.MultipleResourceBundle;

import java.text.MessageFormat;
import java.util.*;

public class Localization
{

	private static LocalizationDelegate delegate;

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
		Localization.bundle = new MultipleResourceBundle(bundles);
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

	private static String formatStringReplace(String message, Object... args)
	{
		int index = 0;
		while(message.contains("{}"))
		{
			if(args.length > index)
			{
				if(args[index] != null)
				{
					message = message.replaceFirst("\\{\\}", args[index].toString());
				}
				else
				{
					message = message.replaceFirst("\\{\\}", "null");
				}
				index++;
			}
			else
			{
				LoggerBridge.error("Args invalid: " + message);
				break;
			}
		}
		return message;
	}

	public static String getString(String key, Object... args)
	{
		final String message = getRawString(key);

		// Use old method
		if(!delegate.useMessageFormatter())
		{
			return formatStringReplace(message, args);
		}
		else
		{
			return MessageFormat.format(message, args);
		}
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

		default boolean useMessageFormatter()
		{
			return false;
		}
	}
}
