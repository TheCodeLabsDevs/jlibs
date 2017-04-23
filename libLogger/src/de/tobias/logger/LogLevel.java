package de.tobias.logger;

public enum LogLevel {

	INFO(0b00001),
	WARNING(0b00010),
	DEBUG(0b00100),
	ERROR(0b01000),
	FATAL(0b10000);

	private int level;

	private LogLevel(int level) {
		this.level = level;
	}

	public int getLevel() {
		return level;
	}
}
