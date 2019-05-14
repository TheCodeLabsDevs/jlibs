package de.thecodelabs.utils;

import de.thecodelabs.utils.util.ColorUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorUtilsTest
{
	@Test
	public void toRGBHexJavaFX()
	{
		final javafx.scene.paint.Color color = new javafx.scene.paint.Color(1.0, 1.0, 1.0, 0.5);
		assertEquals("#FFFFFF7F", ColorUtils.toRGBHex(color));
	}

	@Test
	public void getAppropriateTextColorJavaFX()
	{
		final javafx.scene.paint.Color color = new javafx.scene.paint.Color(1.0, 1.0, 1.0, 1.0);
		final javafx.scene.paint.Color expected = new javafx.scene.paint.Color(0.0, 0.0, 0.0, 1.0);
		assertEquals(expected, ColorUtils.getAppropriateTextColor(color));
	}
}
