package de.tobias.logger;

public class ConsoleUtils {

	public static boolean runningInIntellij() {
		String classPath = System.getProperty("java.class.path");
		return classPath.contains("idea_rt.jar");
	}

	public static enum Color {
		RESET(ConsoleColors.RESET),
		BLACK(ConsoleColors.BLACK),
		RED(ConsoleColors.RED),
		GREEN(ConsoleColors.GREEN),
		YELLOW(ConsoleColors.YELLOW),
		BLUE(ConsoleColors.BLUE),
		PURPLE(ConsoleColors.PURPLE),
		CYAN(ConsoleColors.CYAN),
		WHITE(ConsoleColors.WHITE),
		RED_BRIGHT(ConsoleColors.RED_BRIGHT),
		GREEN_BRIGHT(ConsoleColors.GREEN_BRIGHT),
		YELLOW_BRIGHT(ConsoleColors.GREEN_BRIGHT),
		BLUE_BRIGHT(ConsoleColors.BLUE_BRIGHT),
		PURPLE_BRIGHT(ConsoleColors.PURPLE_BRIGHT),
		CYAN_BRIGHT(ConsoleColors.CYAN_BRIGHT),
		WHITE_BRIGHT(ConsoleColors.WHITE_BRIGHT);

		private String code;

		private Color(String code) {
			this.code = code;
		}
	}

	public static String getConsoleColorCode(ConsoleUtils.Color color) {
		return color.code;
	}

	public static void setConsoleColor(ConsoleUtils.Color color) {
		System.out.print(color.code);
	}

	public static boolean isWindows() {
		return System.getProperty("os.name").contains("Windows");
	}

	public static String stripPackageName(final String classname) {
		String[] parts = classname.split("\\.");
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < parts.length - 1; i++) {
			result.append(parts[i].charAt(0));
			result.append(".");
		}
		result.append(parts[parts.length > 0 ? parts.length - 1 : 0]);
		return result.toString();
	}
}
