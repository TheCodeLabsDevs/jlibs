package de.thecodelabs.utils.localization;

import de.thecodelabs.utils.util.Localization;
import de.thecodelabs.utils.util.localization.LocalizationMessageFormatter;
import de.thecodelabs.utils.util.localization.formatter.JavaMessageFormatter;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class LocalizationMultipleFormatTest implements Localization.LocalizationDelegate
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

	@Override
	public LocalizationMessageFormatter messageFormatter()
	{
		return new JavaMessageFormatter();
	}

	@Before
	public void init()
	{
		Localization.setDelegate(this);
		Localization.load();
	}

	@Test
	public void getStringFromExtended()
	{
		final String string = Localization.getString("strings.extended.format", 2);
		assertEquals("Extended String 2", string);
	}
}
