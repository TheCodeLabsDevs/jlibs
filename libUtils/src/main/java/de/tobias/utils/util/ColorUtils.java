package de.tobias.utils.util;

import javafx.scene.paint.Color;

public class ColorUtils {

	public static String toRGBHex(Color color) {
		return String.format("#%02X%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255), (int) (color.getOpacity() * 255));
	}

	public static String toRGBHexWithoutOpacity(Color color) {
		return String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
	}

}
