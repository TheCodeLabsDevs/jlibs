package de.thecodelabs.utils.localization;

import de.thecodelabs.utils.util.Localization;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class LocalizationSingleTest implements Localization.LocalizationDelegate
{
	@Override
	public Locale getLocale()
	{
		return Locale.GERMAN;
	}

	@Override
	public String getBaseResource()
	{
		return "base";
	}

	@Override
	public boolean useMultipleResourceBundles()
	{
		return false;
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
		assertNotEquals("Extended String", string);
	}
}
