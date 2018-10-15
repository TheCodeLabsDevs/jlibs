package org.slf4j.impl;


import de.tobias.logger.Slf4JLoggerFactory;
import org.slf4j.ILoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.slf4j.spi.LoggerFactoryBinder;

import java.util.logging.LogManager;


public class StaticLoggerBinder implements LoggerFactoryBinder
{
	static
	{
		LogManager.getLogManager().reset();
		SLF4JBridgeHandler.install();
	}

	/**
	 * The unique instance of this class.
	 */
	private static final StaticLoggerBinder SINGLETON = new StaticLoggerBinder();

	/**
	 * Return the singleton of this class.
	 *
	 * @return the StaticLoggerBinder singleton
	 */
	public static final StaticLoggerBinder getSingleton()
	{
		return SINGLETON;
	}


	/**
	 * Declare the version of the SLF4J API this implementation is
	 * compiled against. The value of this field is usually modified
	 * with each release.
	 */
	// To avoid constant folding by the compiler,
	// this field must *not* be final
	public static String REQUESTED_API_VERSION = "1.6";  // !final

	private static final String loggerFactoryClassStr = Slf4JLoggerFactory.class.getName();

	/**
	 * The ILoggerFactory instance returned by the
	 * {@link #getLoggerFactory} method should always be the same
	 * object.
	 */
	private final ILoggerFactory loggerFactory;

	private StaticLoggerBinder()
	{
		loggerFactory = new Slf4JLoggerFactory();
	}

	public ILoggerFactory getLoggerFactory()
	{
		return loggerFactory;
	}

	public String getLoggerFactoryClassStr()
	{
		return loggerFactoryClassStr;
	}
}