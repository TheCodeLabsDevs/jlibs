package de.thecodelabs.utils;

import de.thecodelabs.utils.util.Color;
import de.thecodelabs.utils.util.ColorUtilsNonJavaFX;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorUtilsNonJavaFXTest
{
	@Test
	public void toRGBHex()
	{
		final Color color = new Color(255, 255, 255, 127);
		assertEquals("#FFFFFF7F", ColorUtilsNonJavaFX.toRGBHex(color));
	}

	@Test
	public void toRGBHexWithoutOpacity()
	{
		final Color color = new Color(255, 255, 255);
		assertEquals("#FFFFFF", ColorUtilsNonJavaFX.toRGBHexWithoutOpacity(color));
	}

	@Test
	public void getAppropriateTextColor()
	{
		final Color color = new Color(255, 255, 255);
		final Color expected = new Color(0, 0, 0);
		assertEquals(expected, ColorUtilsNonJavaFX.getAppropriateTextColor(color));
	}
}
