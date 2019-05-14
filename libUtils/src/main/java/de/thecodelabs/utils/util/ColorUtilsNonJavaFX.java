package de.thecodelabs.utils.util;

public class ColorUtilsNonJavaFX
{
	public static String toRGBHex(de.thecodelabs.utils.util.Color color)
	{
		return String.format("#%02X%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public static String toRGBHexWithoutOpacity(de.thecodelabs.utils.util.Color color)
	{
		return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * get an appropriate readable text color for given background color
	 *
	 * @param color - background color
	 * @return Color - text color
	 */
	public static de.thecodelabs.utils.util.Color getAppropriateTextColor(de.thecodelabs.utils.util.Color color)
	{
		// Counting the perceptive luminance - human eye favors green color...
		double a = 1 - (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;

		if(a < 0.5)
		{
			return de.thecodelabs.utils.util.Color.BLACK;
		}
		else
		{
			return de.thecodelabs.utils.util.Color.WHITE;
		}
	}
}
