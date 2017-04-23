package de.tobias.logger;

import java.nio.file.Path;

public class LogMessage {

	private final LogLevel level;
	private final String message;
	private final String callerClass;
	private final Path codeBase;

	public LogMessage(LogLevel level, String message, String callerClass, Path codeBase) {
		this.level = level;
		this.message = message;
		this.callerClass = callerClass;
		this.codeBase = codeBase;
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

	public Path getCodeBase() {
		return codeBase;
	}

}
