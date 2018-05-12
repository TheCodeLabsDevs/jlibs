package de.tobias.utils.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * @author tobias
 *
 */
public class Localization {

	private static LocalizationDelegate delegate;

	private static ResourceBundle bundle;

	/**
	 * 
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
	}

	/**
	 * 
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
	 * 
	 * @return
	 */
	public static ResourceBundle getBundle() {
		return bundle;
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getString(String key) {
		if (bundle != null)
			if (bundle.containsKey(key))
				return bundle.getString(key);
			else {
				System.err.println("Resource Not Found: " + key);
				return key;
			}
		else
			return key + " (bundle nil)";
	}

	/**
	 * 
	 * @param key
	 * @param args
	 * @return
	 */
	public static String getString(String key, Object... args) {
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
						System.err.println("Args invalid: " + key);
						break;
					}
				}
				return message;
			} else {
				System.err.println("Resource Not Found: " + key);
				return key;
			}
		else
			return key + " (bundle nil)";
	}

	/**
	 * 
	 * @author tobias
	 *
	 */
	public interface LocalizationDelegate {

		/**
		 * 
		 * @return
		 */
		public Locale getLocale();

		/**
		 * 
		 * @return
		 */
		public String getBaseResource();
	}
}
