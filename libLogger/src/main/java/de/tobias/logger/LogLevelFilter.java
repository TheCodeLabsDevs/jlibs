package de.tobias.logger;

public enum LogLevelFilter {

	TRACE(0b111111),
	DEBUG(0b111110),
	ERROR(0b110000),
	WARNING(0b111000),
	NORMAL(0b111100);

	private int matchedLevel;

	LogLevelFilter(int matchedLevel)
	{
		this.matchedLevel = matchedLevel;
	}

	public boolean acceptLevel(LogLevel level) {
		return (matchedLevel & level.getLevel()) != 0;
	}
}
