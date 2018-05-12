package de.tobias.utils.util;

public class ConsoleUtils {

	public static enum Color {
		RESET("\u001B[0m"),
		YELLOW("\u001B[33m"),
		RED("\u001B[31m"),
		BLUE("\u001B[34m"),
		GREEN("\u001B[32m"),
		BLACK("\u001B[30m"),
		PURPLE("\u001B[35m"),
		CYAN("\u001B[36m"),
		WHITE("\u001B[37m");

		private String code;

		private Color(String code) {
			this.code = code;
		}
	}

	public static String getConsoleColorCode(Color color) {
		return color.code;
	}

	public static void setConsoleColor(Color color) {
		System.out.print(color.code);
	}
}
