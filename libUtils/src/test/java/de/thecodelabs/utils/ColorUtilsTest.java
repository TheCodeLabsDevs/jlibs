package de.thecodelabs.utils;

import de.thecodelabs.utils.util.Color;
import de.thecodelabs.utils.util.ColorUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ColorUtilsTest
{
	@Test
	public void toRGBHex()
	{
		final Color color = new Color(255, 255, 255, 127);
		assertEquals("#FFFFFF7F", ColorUtils.toRGBHex(color));
	}

	@Test
	public void toRGBHexWithoutOpacity()
	{
		final Color color = new Color(255, 255, 255);
		assertEquals("#FFFFFF", ColorUtils.toRGBHexWithoutOpacity(color));
	}

	@Test
	public void getAppropriateTextColor()
	{
		final Color color = new Color(255, 255, 255);
		final Color expected = new Color(0, 0, 0);
		System.out.println(ColorUtils.getAppropriateTextColor(color));
		assertEquals(expected, ColorUtils.getAppropriateTextColor(color));
	}

	@Test
	public void toRGBHexJavaFX()
	{
		final javafx.scene.paint.Color color = new javafx.scene.paint.Color(1.0, 1.0, 1.0, 0.5);
		assertEquals("#FFFFFF7F", ColorUtils.toRGBHex(color));
	}

	@Test
	public void toRGBHexWithoutOpacityJavaFX()
	{
		final javafx.scene.paint.Color color = new javafx.scene.paint.Color(1.0, 1.0, 1.0, 0.5);
		assertEquals("#FFFFFF", ColorUtils.toRGBHexWithoutOpacity(color));
	}

	@Test
	public void getAppropriateTextColorJavaFX()
	{
		final javafx.scene.paint.Color color = new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1.0);
		final javafx.scene.paint.Color expected = new javafx.scene.paint.Color(0.0, 0.0, 0.0, 1.0);
		assertEquals(expected, ColorUtils.getAppropriateTextColor(color));
	}
}
