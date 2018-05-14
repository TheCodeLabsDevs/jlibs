package de.tobias.logger;

import de.tobias.utils.settings.YAMLSettings;
import de.tobias.utils.util.ConsoleUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

public class Logger {

	private static LogLevelFilter levelFilter;
	private static List<LogFilter> filters;

	private static ConsoleStream outputStream;
	private static ConsoleStream errorStream;
	private static boolean initialized = false;

	private static LoggerConfig loggerConfig;

	static {
		filters = new LinkedList<>();
		levelFilter = LogLevelFilter.NORMAL;
	}

	public static void init(Path path) {
		if (initialized) {
			System.err.println("Logger is already initialized.");
			return;
		}
		try {
			if (Files.notExists(path))
				Files.createDirectories(path);

			loggerConfig = loadLoggerConfig();

			outputStream = new ConsoleStream(path.resolve("out.log"), System.out, LogLevel.INFO, loggerConfig);
			errorStream = new ConsoleStream(path.resolve("err.log"), System.err, LogLevel.ERROR, loggerConfig);

			System.setOut(outputStream);
			System.setErr(errorStream);

			initialized = true;
		} catch (IOException e) {
			System.err.println("Failed to initialize logger: " + e.toString());
		}
	}

	private static LoggerConfig loadLoggerConfig() {
		final InputStream inputStream = Logger.class.getClassLoader().getResourceAsStream("libLogger.yml");
		if (inputStream != null) {
			return YAMLSettings.load(LoggerConfig.class, inputStream);
		} else {
			return new LoggerConfig();
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
			System.err.println("initialize logger first (Logger.init(Path))");
			return;
		}
		if (levelFilter.acceptLevel(level)) {

			StackTraceElement element = Thread.currentThread().getStackTrace()[2];
			String className = element.getClassName();

			LogMessage logMessage = new LogMessage(level, message, className);
			boolean cancelMessage = filters.stream().anyMatch(f -> !f.accept(logMessage));
			if (!cancelMessage) {
				if (level == LogLevel.ERROR || level == LogLevel.FATAL) {
					System.err.println(logMessage.buildString(loggerConfig));
				} else {
					System.out.println(logMessage.buildString(loggerConfig));
				}
			}
		}
	}
}
