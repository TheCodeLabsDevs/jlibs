package de.thecodelabs.logger;

import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.helpers.MessageFormatter;

public class Slf4JLoggerAdapter implements Logger
{

	private static boolean disableDebug;

	public static void disableSlf4jDebugPrints()
	{
		de.thecodelabs.logger.Logger.addFilter(logMessage -> !(logMessage.getCaller().getClassName().contains("Slf4jLog") &&
				(logMessage.getLevel() == LogLevel.DEBUG || logMessage.getLevel() == LogLevel.TRACE)));
		disableDebug = true;
	}

	public String getName()
	{
		return "libLogger";
	}

	public boolean isTraceEnabled()
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.TRACE) && !disableDebug;
	}

	public void trace(String s)
	{
		de.thecodelabs.logger.Logger.trace(s);
	}

	public void trace(String s, Object o)
	{
		de.thecodelabs.logger.Logger.trace(MessageFormatter.format(s, o).getMessage());
	}

	public void trace(String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.trace(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void trace(String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.trace(MessageFormatter.format(s, objects).getMessage());
	}

	public void trace(String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.trace(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isTraceEnabled(Marker marker)
	{
		return false;
	}

	public void trace(Marker marker, String s)
	{

	}

	public void trace(Marker marker, String s, Object o)
	{

	}

	public void trace(Marker marker, String s, Object o, Object o1)
	{

	}

	public void trace(Marker marker, String s, Object... objects)
	{

	}

	public void trace(Marker marker, String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.trace(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isDebugEnabled()
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.DEBUG) && !disableDebug;
	}

	public void debug(String s)
	{
		de.thecodelabs.logger.Logger.debug(s);
	}

	public void debug(String s, Object o)
	{
		de.thecodelabs.logger.Logger.debug(MessageFormatter.format(s, o).getMessage());
	}

	public void debug(String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.debug(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void debug(String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.debug(MessageFormatter.format(s, objects).getMessage());
	}

	public void debug(String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.debug(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isDebugEnabled(Marker marker)
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.DEBUG) && !disableDebug;
	}

	public void debug(Marker marker, String s)
	{
		de.thecodelabs.logger.Logger.debug(s);
	}

	public void debug(Marker marker, String s, Object o)
	{
		de.thecodelabs.logger.Logger.debug(MessageFormatter.format(s, o).getMessage());
	}

	public void debug(Marker marker, String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.debug(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void debug(Marker marker, String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.debug(MessageFormatter.format(s, objects).getMessage());
	}

	public void debug(Marker marker, String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.debug(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isInfoEnabled()
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.INFO);
	}

	public void info(String s)
	{
		de.thecodelabs.logger.Logger.info(s);
	}

	public void info(String s, Object o)
	{
		de.thecodelabs.logger.Logger.info(MessageFormatter.format(s, o).getMessage());
	}

	public void info(String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.info(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void info(String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.info(MessageFormatter.format(s, objects).getMessage());
	}

	public void info(String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.info(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isInfoEnabled(Marker marker)
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.INFO);
	}

	public void info(Marker marker, String s)
	{
		de.thecodelabs.logger.Logger.info(s);
	}

	public void info(Marker marker, String s, Object o)
	{
		de.thecodelabs.logger.Logger.info(MessageFormatter.format(s, o).getMessage());
	}

	public void info(Marker marker, String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.info(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void info(Marker marker, String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.info(MessageFormatter.format(s, objects).getMessage());
	}

	public void info(Marker marker, String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.info(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isWarnEnabled()
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.WARNING);
	}

	public void warn(String s)
	{
		de.thecodelabs.logger.Logger.warning(s);
	}

	public void warn(String s, Object o)
	{
		de.thecodelabs.logger.Logger.warning(MessageFormatter.format(s, o).getMessage());
	}

	public void warn(String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.warning(MessageFormatter.format(s, objects).getMessage());
	}

	public void warn(String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.warning(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void warn(String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.warning(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isWarnEnabled(Marker marker)
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.WARNING);
	}

	public void warn(Marker marker, String s)
	{
		de.thecodelabs.logger.Logger.warning(s);
	}

	public void warn(Marker marker, String s, Object o)
	{
		de.thecodelabs.logger.Logger.warning(MessageFormatter.format(s, o).getMessage());
	}

	public void warn(Marker marker, String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.warning(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void warn(Marker marker, String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.warning(MessageFormatter.format(s, objects).getMessage());
	}

	public void warn(Marker marker, String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.warning(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isErrorEnabled()
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.ERROR);
	}

	public void error(String s)
	{
		de.thecodelabs.logger.Logger.error(s);
	}

	public void error(String s, Object o)
	{
		de.thecodelabs.logger.Logger.error(MessageFormatter.format(s, o).getMessage());
	}

	public void error(String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.error(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void error(String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.error(MessageFormatter.format(s, objects).getMessage());
	}

	public void error(String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.error(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}

	public boolean isErrorEnabled(Marker marker)
	{
		return de.thecodelabs.logger.Logger.getLevelFilter().acceptLevel(LogLevel.ERROR);
	}

	public void error(Marker marker, String s)
	{
		de.thecodelabs.logger.Logger.error(s);
	}

	public void error(Marker marker, String s, Object o)
	{
		de.thecodelabs.logger.Logger.error(MessageFormatter.format(s, o).getMessage());
	}

	public void error(Marker marker, String s, Object o, Object o1)
	{
		de.thecodelabs.logger.Logger.error(MessageFormatter.format(s, o, o1).getMessage());
	}

	public void error(Marker marker, String s, Object... objects)
	{
		de.thecodelabs.logger.Logger.error(MessageFormatter.format(s, objects).getMessage());
	}

	public void error(Marker marker, String s, Throwable throwable)
	{
		de.thecodelabs.logger.Logger.error(s);
		if(throwable != null)
		{
			de.thecodelabs.logger.Logger.error(throwable);
		}
	}
}
