package de.tobias.logger;

import java.text.SimpleDateFormat;

public class LogMessage {

	private final LogLevel level;
	private final String message;
	private final String callerClass;

	public LogMessage(LogLevel level, String message, String callerClass) {
		this.level = level;
		this.message = message;
		this.callerClass = callerClass;
	}

	public LogLevel getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

	public String getCallerClass() {
		return callerClass;
	}


	public String buildString(LoggerConfig loggerConfig) {
		final SimpleDateFormat format = new SimpleDateFormat(loggerConfig.getDateFormatterPattern());
		long time = System.currentTimeMillis();

		StringBuilder builder = new StringBuilder();

		// Log Level
		if (loggerConfig.isColorEnabled()) {
			ConsoleUtils.Color color = getColorForLogLevel(level, loggerConfig);
			if (color != null) {
				builder.append(ConsoleUtils.getConsoleColorCode(color));
			} else {
				builder.append(ConsoleUtils.getConsoleColorCode(ConsoleUtils.Color.RESET));
			}
		}
		builder.append(String.format("%9s", getLevelString(level)));
		builder.append(" ");

		// Time
		if (loggerConfig.isColorEnabled()) {
			builder.append(ConsoleUtils.getConsoleColorCode(loggerConfig.getTimeColor()));
		}
		builder.append(format.format(time));

		if (loggerConfig.isColorEnabled()) {
			builder.append(ConsoleUtils.getConsoleColorCode(ConsoleUtils.Color.RESET));
		}

		if (callerClass != null) {
			builder.append(" @ ");

			// Class Name
			if (loggerConfig.isColorEnabled()) {
				builder.append(ConsoleUtils.getConsoleColorCode(loggerConfig.getDetailColor()));
			}
			builder.append(getClassName(callerClass));
		}

		if (loggerConfig.isColorEnabled()) {
			builder.append(ConsoleUtils.getConsoleColorCode(ConsoleUtils.Color.RESET));
		}
		builder.append(": ");

		// Message
		if (loggerConfig.isColorEnabled()) {
			builder.append(ConsoleUtils.getConsoleColorCode(loggerConfig.getMessageColor()));
		}
		builder.append(message);
		return builder.toString();
	}

	private static String getLevelString(LogLevel level) {
		return "[" + level.name() + "]";
	}

	private static String getClassName(String className) {
		return "[" + className + "]";
	}

	private static ConsoleUtils.Color getColorForLogLevel(LogLevel logLevel, LoggerConfig loggerConfig) {
		switch (logLevel) {
			case INFO:
			case DEBUG:
				return loggerConfig.getInfoColor();
			case WARNING:
				return loggerConfig.getWarnColor();
			case ERROR:
			case FATAL:
				return loggerConfig.getErrorColor();
			default:
				return null;
		}
	}
}
