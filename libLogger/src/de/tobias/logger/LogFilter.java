package de.tobias.logger;


public interface LogFilter {

	boolean accept(LogMessage logMessage);
}
