package de.tobias.logger;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.Storable;

public class LoggerConfig implements SettingsSerializable {

	@Storable
	private Boolean color = true;
	@Storable
	private String infoColor = ConsoleUtils.Color.GREEN.name();
	@Storable
	private String warnColor = ConsoleUtils.Color.YELLOW.name();
	@Storable
	private String errorColor = ConsoleUtils.Color.RED.name();
	@Storable
	private String timeColor = ConsoleUtils.Color.WHITE.name();
	@Storable
	private String detailColor = ConsoleUtils.Color.CYAN.name();
	@Storable
	private String messageColor = ConsoleUtils.Color.RESET.name();
	@Storable
	private String dateFormatterPattern = "dd-MM-YY HH:mm:ss";
	@Storable
	private String defaultOutLevel = LogLevel.INFO.name();
	@Storable
	private String defaultErrLevel = LogLevel.ERROR.name();

	Boolean isColorEnabled() {
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
}
