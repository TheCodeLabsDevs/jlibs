package de.tobias.utils.util;

public class NumberUtils {

	public static String numberToString(double number) {
		return numberToString(number, 2);
	}

	public static String numberToString(double number, int minor) {
		if (number > 1000L * 1000L * 1000L * 1000L) {
			return String.format("%1." + minor + "f", number / (1000L * 1000L * 1000L * 1000L)) + " T";
		} else if (number > 1000L * 1000L * 1000L) {
			return String.format("%1." + minor + "f", number / (1000L * 1000L * 1000L)) + " G";
		} else if (number > 1000L * 1000L) {
			return String.format("%1." + minor + "f", number / (1000L * 1000L)) + " M";
		} else if (number > 1000L) {
			return String.format("%1." + minor + "f", number / (1000L)) + " K";
		}
		return String.format("%1." + minor + "f", number) + " ";
	}
}
