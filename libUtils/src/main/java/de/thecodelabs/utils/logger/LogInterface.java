package de.thecodelabs.utils.logger;

public interface LogInterface
{
	void trace(Object obj);

	void debug(Object obj);

	void info(Object obj);

	void warning(Object obj);

	void error(Object obj);

	void error(Throwable e);

	void error(Object obj, Throwable e);

	void fatal(Object obj);
}
