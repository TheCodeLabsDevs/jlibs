package de.thecodelabs.utils.util.localization;

import java.util.*;

public class MultipleResourceBundle extends ResourceBundle
{
	private final Locale locale;
	private Map<String, String> values = new HashMap<>();

	public MultipleResourceBundle(List<ResourceBundle> resourceBundles, Locale locale)
	{
		this.locale = locale;
		for(ResourceBundle bundle : resourceBundles)
		{
			addResourceBundle(bundle);
		}
	}

	public MultipleResourceBundle(MultipleResourceBundle oldList, ResourceBundle... resourceBundles) {
		locale = oldList.locale;
		values = oldList.values;
		for(ResourceBundle bundle : resourceBundles)
		{
			addResourceBundle(bundle);
		}
	}

	private void addResourceBundle(ResourceBundle bundle)
	{
		for(String key : bundle.keySet())
		{
			values.put(key, bundle.getString(key));
		}
	}

	@Override
	public Locale getLocale()
	{
		return locale;
	}

	@Override
	protected Object handleGetObject(String key)
	{
		return values.get(key);
	}

	@Override
	public Enumeration<String> getKeys()
	{
		return new ResourceBundleEnumeration(values.keySet(), null);
	}
}
