package de.tobias.logger;

import de.tobias.utils.settings.YAMLSettings;
import de.tobias.utils.util.OS;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class Logger {

	private static final String OUT_FILE = "out.log";
	private static final String ERR_FILE = "err.log";

	private static LogLevelFilter levelFilter;
	private static List<LogFilter> filters;

	private static Path baseDir;
	private static ConsoleStream outputStream;
	private static ConsoleStream errorStream;

	private static boolean initialized = false;

	private static LoggerConfig loggerConfig;

	static {
		filters = new LinkedList<>();
		levelFilter = LogLevelFilter.NORMAL;
	}

	public static void init(Path baseDir) {
		if (initialized) {
			System.err.println("Logger is already initialized.");
			return;
		}
		try {
			if (Files.notExists(baseDir))
				Files.createDirectories(baseDir);

			Logger.baseDir = baseDir;
			loggerConfig = loadLoggerConfig();
			initialized = true;

			// Allow Windows to use colors in prompt
			if (loggerConfig.isColorEnabled() && OS.isWindows()) {
				AnsiConsole.systemInstall();
			}

			setFileOutput(FileOutputOption.DISABLED, System.out, System.err);
		} catch (IOException e) {
			System.err.println("Failed to initialize logger: " + e.toString());
		}
	}

	private static LoggerConfig loadLoggerConfig() {
		InputStream inputStream = Logger.class.getClassLoader().getResourceAsStream("libLogger.yml");
		if (inputStream == null) {
			inputStream = Logger.class.getClassLoader().getResourceAsStream("config/libLogger.yml");
		}
		if (inputStream != null) {
			return YAMLSettings.load(LoggerConfig.class, inputStream);
		} else {
			return new LoggerConfig();
		}
	}

	public static void setFileOutput(FileOutputOption fileOutputOption) {
		setFileOutput(fileOutputOption, outputStream.getSource(), errorStream.getSource());
	}

	private static void setFileOutput(FileOutputOption fileOutputOption, PrintStream standardOut, PrintStream standardError) {
		if (initialized) {
			try {
				Path outFile, errFile;
				if (fileOutputOption == FileOutputOption.COMBINED) {
					outFile = baseDir.resolve(OUT_FILE);
					errFile = baseDir.resolve(OUT_FILE);
				} else {
					outFile = baseDir.resolve(OUT_FILE);
					errFile = baseDir.resolve(ERR_FILE);
				}

				outputStream = new ConsoleStream(outFile, standardOut, LogLevel.INFO, loggerConfig);
				errorStream = new ConsoleStream(errFile, standardError, LogLevel.ERROR, loggerConfig);

				System.setOut(outputStream);
				System.setErr(errorStream);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}

			outputStream.setFileOutput(fileOutputOption != FileOutputOption.DISABLED);
			errorStream.setFileOutput(fileOutputOption != FileOutputOption.DISABLED);
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
	 * @param level log level
	 * @param any   object to log
	 */
	public static void log(LogLevel level, Object any) {
		if (!initialized) {
			System.err.println("initialize logger first (Logger.init(Path))");
			return;
		}
		if (levelFilter.acceptLevel(level)) {

			String className = null;
			final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

			for (int i = 2; i < stackTrace.length; i++) {
				StackTraceElement element = stackTrace[i];
				if (!element.getClassName().contains(Logger.class.getPackage().getName())) {
					className = element.getClassName();
					break;
				}
			}


			LogMessage logMessage = new LogMessage(level, any != null ? any.toString() : "null", className);
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

	public static void info(Object any) {
		log(LogLevel.INFO, any.toString());
	}

	public static void debug(Object any) {
		log(LogLevel.DEBUG, any.toString());
	}

	public static void error(Object any) {
		log(LogLevel.ERROR, any.toString());
	}

	public static void warning(Object any) {
		log(LogLevel.WARNING, any.toString());
	}

	public static void fatal(Object any) {
		log(LogLevel.FATAL, any.toString());
	}

	public static void error(Throwable throwable) {
		log(LogLevel.ERROR, getStringFromException(throwable));
	}

	private static String getStringFromException(Throwable e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

}
