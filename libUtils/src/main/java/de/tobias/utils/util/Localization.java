package de.tobias.utils.util;

import de.tobias.utils.logger.LoggerBridge;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author tobias
 */
public class Localization {

	private static LocalizationDelegate delegate;

	private static ResourceBundle bundle;

	/**
	 * @param delegate
	 */
	public static void setDelegate(LocalizationDelegate delegate) {
		Localization.delegate = delegate;
	}

	/**
	 *
	 */
	public static void load() {
		if (delegate == null) {
			throw new NullPointerException("Delegate is null. Use: Localization.setDelegate()");
		}
		bundle = loadBundle(delegate.getBaseResource(), Localization.class.getClassLoader());
		LoggerBridge.debug("Loaded localization bundle: " + bundle.getBaseBundleName() + " for language: " + bundle.getLocale());
	}

	/**
	 * @param name
	 * @param loader
	 * @return
	 */
	public static ResourceBundle loadBundle(String name, ClassLoader loader) {
		Locale locale = delegate != null ? delegate.getLocale() : Locale.GERMAN;
		try {
			return ResourceBundle.getBundle(name, locale, loader);
		} catch (MissingResourceException e) {
			return ResourceBundle.getBundle(name, Locale.GERMAN, loader);
		}
	}

	/**
	 * @return
	 */
	public static ResourceBundle getBundle() {
		return bundle;
	}

	/**
	 * @param key
	 * @return
	 */
	private static String _getString(String key) {
		if (bundle != null)
			if (bundle.containsKey(key))
				return bundle.getString(key);
			else {
				LoggerBridge.error("Resource Not Found: " + key);
				return key;
			}
		else
			return key + " (bundle nil)";
	}

	/**
	 * @param key
	 * @param args
	 * @return
	 */
	private static String _getString(String key, Object... args) {
		if (bundle != null)
			if (bundle.containsKey(key)) {
				String message = bundle.getString(key);
				int index = 0;
				while (message.contains("{}")) {
					if (args.length > index) {
						if (args[index] != null) {
							message = message.replaceFirst("\\{\\}", args[index].toString());
						} else {
							message = message.replaceFirst("\\{\\}", "null");
						}
						index++;
					} else {
						LoggerBridge.error("Args invalid: " + key);
						break;
					}
				}
				return message;
			} else {
				LoggerBridge.error("Resource Not Found: " + key);
				return key;
			}
		else
			return key + " (bundle nil)";
	}

	public static String getString(String key) {
		// Use old method
		if (!delegate.useMessageFormatter()) {
			return _getString(key);
		}

		if (bundle == null) {
			throw new NullPointerException("ResourceBundle is null. Call Localization.init() and Localization.loadLanguage() first");
		}

		if (bundle.containsKey(key)) {
			return bundle.getString(key);
		} else {
			LoggerBridge.error("Unknown key for ResourceBundle: " + key);
			return key;
		}
	}

	public static String getString(String key, Object... args) {
		// Use old method
		if (!delegate.useMessageFormatter()) {
			return _getString(key, args);
		}

		if (bundle == null) {
			throw new NullPointerException("ResourceBundle is null. Call Localization.init() and Localization.loadLanguage() first");
		}

		if (bundle.containsKey(key)) {
			return MessageFormat.format(bundle.getString(key), args);
		} else {
			LoggerBridge.error("Unknown key for ResourceBundle: " + key);
			return key;
		}
	}


	public interface LocalizationDelegate {

		Locale getLocale();

		String getBaseResource();

		default boolean useMessageFormatter() {
			return false;
		}
	}
}
