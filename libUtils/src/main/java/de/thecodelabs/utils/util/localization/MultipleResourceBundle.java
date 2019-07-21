package de.thecodelabs.utils.util.localization;

import java.util.*;

public class MultipleResourceBundle extends ResourceBundle
{
	private Map<String, String> values = new HashMap<>();

	public MultipleResourceBundle(List<ResourceBundle> resourceBundles)
	{
		for(ResourceBundle bundle : resourceBundles)
		{
			for(String key : bundle.keySet())
			{
				values.put(key, bundle.getString(key));
			}
		}
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
