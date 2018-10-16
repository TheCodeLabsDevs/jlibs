package de.thecodelabs.logger;

import java.text.SimpleDateFormat;

public class LogMessage {

	private final LogLevel level;
	private final String message;

	private final StackTraceElement caller;

	public LogMessage(LogLevel level, String message, StackTraceElement caller) {
		this.level = level;
		this.message = message;
		this.caller = caller;
	}

	public LogLevel getLevel() {
		return level;
	}

	public String getMessage() {
		return message;
	}

	public StackTraceElement getCaller() {
		return caller;
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

		if (caller != null && loggerConfig.showCallInformation()) {
			builder.append(" @ ");

			// Class Name
			if (loggerConfig.isColorEnabled()) {
				builder.append(ConsoleUtils.getConsoleColorCode(loggerConfig.getDetailColor()));
			}

			builder.append(getClassName(caller, loggerConfig));
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

	private static String getClassName(StackTraceElement caller, LoggerConfig config) {
		StringBuilder builder = new StringBuilder();
		builder.append("[");
		if (config.isShowClassName()) {
			String className = caller.getClassName();

			if (config.isShowShortPackageName()) {
				className = ConsoleUtils.stripPackageName(className);
			}

			builder.append(className);
			if (config.isShowMethodName()) {
				builder.append(".");
			}
		}
		if (config.isShowMethodName()) {
			builder.append(caller.getMethodName());
			if (config.isShowLineNumber()) {
				builder.append(":");
			}
		}
		if (config.isShowLineNumber()) {
			builder.append(caller.getLineNumber());
		}
		builder.append("]");
		return builder.toString();
	}

	private static ConsoleUtils.Color getColorForLogLevel(LogLevel logLevel, LoggerConfig loggerConfig) {
		switch (logLevel) {
			case INFO:
			case DEBUG:
			case TRACE:
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
