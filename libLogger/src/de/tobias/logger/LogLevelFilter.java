package de.tobias.logger;

public enum LogLevelFilter {

	DEBUG(0b11111),
	ERROR(0b11000),
	WARNING(0b11100),
	NORMAL(0b11110);

	private int matchedLevel;

	private LogLevelFilter(int matchedLevel) {
		this.matchedLevel = matchedLevel;
	}

	public boolean acceptLevel(LogLevel level) {
		return (matchedLevel & level.getLevel()) != 0;
	}
}
