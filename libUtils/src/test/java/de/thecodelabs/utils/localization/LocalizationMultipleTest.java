package de.thecodelabs.utils.localization;

import de.thecodelabs.utils.util.Localization;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class LocalizationMultipleTest implements Localization.LocalizationDelegate
{
	@Override
	public Locale getLocale()
	{
		return Locale.GERMAN;
	}

	@Override
	public String[] getBaseResources()
	{
		return new String[]{
				"base",
				"extended"
		};
	}

	@Override
	public boolean useMultipleResourceBundles()
	{
		return true;
	}

	@Before
	public void init()
	{
		Localization.setDelegate(this);
		Localization.load();
	}

	@Test
	public void getStringFromBase()
	{
		final String string = Localization.getString("strings.base");
		assertEquals("Base String", string);
	}

	@Test
	public void getStringFromExtended()
	{
		final String string = Localization.getString("strings.extended");
		assertEquals("Extended String", string);
	}

	@Test
	public void getStringFromExtendedReplace()
	{
		final String string = Localization.getString("strings.extended.replace", 3);
		assertEquals("Extended String 3", string);
	}
}
