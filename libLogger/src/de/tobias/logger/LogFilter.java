package de.tobias.logger;


public interface LogFilter {

	public boolean accept(LogMessage logMessage);
}
