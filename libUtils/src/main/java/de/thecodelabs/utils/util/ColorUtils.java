package de.thecodelabs.utils.util;

import javafx.scene.paint.Color;

public class ColorUtils {

	public static String toRGBHex(Color color) {
		return String.format("#%02X%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), (int) (color.getOpacity() * 255));
	}

	public static String toRGBHexWithoutOpacity(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
	}

	/**
	 * get an appropriate readable text color for given background color
	 *
	 * @param color - background color
	 * @return Color - text color
	 */
	public static Color getAppropriateTextColor(Color color) {
		// Counting the perceptive luminance - human eye favors green color...
		double a = 1 - (0.299 * (int) (color.getRed() * 255) + 0.587 * (int) (color.getGreen() * 255) + 0.114 * (int) (color.getBlue() * 255)) / 255;

		if (a < 0.5) {
			return Color.BLACK;
		} else {
			return Color.WHITE;
		}
	}

}
