package de.tobias.utils.util;

public class NumberUtils {

	public static String convertBytesToAppropriateFormat(double number) {
		return convertBytesToAppropriateFormat(number, 2);
	}

	public static String convertBytesToAppropriateFormat(double number, int minor) {
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

	/**
	 * Konvertiert Bytes zu MB, KB und Bytes
	 *
	 * @param bytes long - Bytes
	 * @return String - Megabyte + Kilobyte + Byte;
	 */
	public static String convertBytesToDecimalSize(long bytes) {
		long normal = bytes % 1000;
		long kilo = (bytes / 1000) % 1000;
		long mega = (bytes / 1000000) % 1000000;

		return mega + " MB " + kilo + " KB " + normal;
	}

	/**
	 * Konvertiert Bytes zu MB, KB und Bytes
	 *
	 * @param bytes long - Bytes
	 * @return String - Megabyte + Kilobyte + Byte;
	 */
	public static String convertBytesToBinarySize(long bytes) {
		long normal = bytes % 1024;
		long kilo = (bytes / 1024) % 1024;
		long mega = (bytes / 1048576) % 1048576;

		return mega + " MB " + kilo + " KB " + normal;
	}


}
