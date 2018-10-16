package de.thecodelabs.logger;


public interface LogFilter {

	boolean accept(LogMessage logMessage);
}
