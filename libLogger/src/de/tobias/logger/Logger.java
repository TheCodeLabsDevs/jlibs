package de.tobias.logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class Logger {

	private static LogLevelFilter levelFilter;
	private static List<LogFilter> filters;
	private static final SimpleDateFormat format = new SimpleDateFormat("dd-MM-YY HH-mm-ss");

	private static ConsoleStream outputStream;
	private static ConsoleStream errorStream;
	private static boolean initialized = false;

	static {
		filters = new LinkedList<>();
		levelFilter = LogLevelFilter.NORMAL;
	}

	public static void init(Path path) {
		if (initialized) {
			System.err.println("Logger is already initalized.");
			return;
		}
		try {
			if (Files.notExists(path))
				Files.createDirectories(path);

			outputStream = new ConsoleStream(path.resolve("out.log"), System.out, LogLevel.INFO);
			errorStream = new ConsoleStream(path.resolve("err.log"), System.err, LogLevel.ERROR);

			System.setOut(outputStream);
			System.setErr(errorStream);

			initialized = true;
		} catch (IOException e) {
			System.err.println("Failed to initalize logger: " + e.toString());
		}
	}

	public static void enableFileOutput(boolean enable) {
		if (initialized) {
			outputStream.setFileOutput(enable);
			errorStream.setFileOutput(enable);
		}
	}

	public static void setLevelFilter(LogLevelFilter levelFilter) {
		Logger.levelFilter = levelFilter;
	}

	public static void addFilter(LogFilter f) {
		filters.add(f);
	}

	public static void removeFilter(LogFilter f) {
		filters.remove(f);
	}

	/**
	 * Logs a message into a special channel.
	 *
	 * @param level   log level
	 * @param message message
	 */
	public static void log(LogLevel level, String message) {
		if (!initialized) {
			System.err.println("Initalze logger first (Logger.init(Path))");
			return;
		}
		if (levelFilter.acceptLevel(level)) {

			StackTraceElement element = Thread.currentThread().getStackTrace()[2];
			String className = element.getClassName();
			String codeBase = null;
			try {
				Class<?> clazz = Class.forName(className);
				codeBase = clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
			} catch (ClassNotFoundException ignored) {
			}

			LogMessage logMessage = new LogMessage(level, message, className, codeBase);
			boolean cancelMessage = filters.stream().anyMatch(f -> !f.accept(logMessage));
			if (!cancelMessage) {
				if (level == LogLevel.ERROR || level == LogLevel.FATAL) {
					System.err.println(buildString(level, message, className));
				} else {
					System.out.println(buildString(level, message, className));
				}
			}
		}
	}

	static String buildString(LogLevel level, String message, String className) {
		StringBuilder builder = new StringBuilder();

		// Log Level
		String levelBuilder = "[" +
				level.name() +
				"]";
		builder.append(String.format("%9s", levelBuilder));

		builder.append(" ");

		long time = System.currentTimeMillis();
		builder.append(format.format(time));
		builder.append(" @ ");

		builder.append("[");
		builder.append(className);
		builder.append("]");
		builder.append(": ");

		builder.append(message);
		return builder.toString();
	}
}
