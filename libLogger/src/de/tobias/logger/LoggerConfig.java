package de.tobias.logger;

import de.tobias.utils.settings.Key;

public class LoggerConfig {

	@Key("color.enable")
	private boolean color = true;
	@Key("color.info")
	private String infoColor = ConsoleUtils.Color.GREEN.name();
	@Key("color.warn")
	private String warnColor = ConsoleUtils.Color.YELLOW.name();
	@Key("color.error")
	private String errorColor = ConsoleUtils.Color.RED.name();
	@Key("color.time")
	private String timeColor = ConsoleUtils.Color.WHITE.name();
	@Key("color.detail")
	private String detailColor = ConsoleUtils.Color.CYAN.name();
	@Key("color.message")
	private String messageColor = ConsoleUtils.Color.RESET.name();

	@Key
	private String dateFormatterPattern = "dd-MM-YY HH:mm:ss";

	@Key
	private String defaultOutLevel = LogLevel.INFO.name();
	@Key
	private String defaultErrLevel = LogLevel.ERROR.name();

	@Key
	private boolean showShortPackageName = true;
	@Key
	private boolean showClassName = true;
	@Key
	private boolean showMethodName = false;
	@Key
	private boolean showLineNumber = false;

	@Key
	private boolean ignoreStandardStream = false;

	boolean isColorEnabled() {
		return color;
	}

	ConsoleUtils.Color getInfoColor() {
		return ConsoleUtils.Color.valueOf(infoColor);
	}

	ConsoleUtils.Color getWarnColor() {
		return ConsoleUtils.Color.valueOf(warnColor);
	}

	ConsoleUtils.Color getErrorColor() {
		return ConsoleUtils.Color.valueOf(errorColor);
	}

	ConsoleUtils.Color getTimeColor() {
		return ConsoleUtils.Color.valueOf(timeColor);
	}

	ConsoleUtils.Color getDetailColor() {
		return ConsoleUtils.Color.valueOf(detailColor);
	}

	ConsoleUtils.Color getMessageColor() {
		return ConsoleUtils.Color.valueOf(messageColor);
	}

	String getDateFormatterPattern() {
		return dateFormatterPattern;
	}

	LogLevel getDefaultOutLevel() {
		return LogLevel.valueOf(defaultOutLevel);
	}

	LogLevel getDefaultErrLevel() {
		return LogLevel.valueOf(defaultErrLevel);
	}

	boolean isShowShortPackageName() {
		return showShortPackageName;
	}

	boolean isShowClassName() {
		return showClassName;
	}

	boolean isShowMethodName() {
		return showMethodName;
	}

	boolean isShowLineNumber() {
		return showLineNumber;
	}

	boolean isIgnoreStandardStream() {
		return ignoreStandardStream;
	}

	boolean showCallInformation() {
		return showClassName || showMethodName || showLineNumber;
	}
}
