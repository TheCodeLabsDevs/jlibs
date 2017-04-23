package de.tobias.logger;

public enum LogLevelFilter {

	DEBUG(0b11111),
	ERROR(0b11011),
	NORMAL(0b00011);

	private int matchedLevel;

	private LogLevelFilter(int matchedLevel) {
		this.matchedLevel = matchedLevel;
	}

	public boolean acceptLevel(LogLevel level) {
		return (matchedLevel & level.getLevel()) != 0;
	}
}
