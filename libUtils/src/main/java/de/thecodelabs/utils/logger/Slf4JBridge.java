package de.thecodelabs.utils.logger;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

@SuppressWarnings("java:S2629")
public class Slf4JBridge implements LogInterface
{
	private static final Logger LOGGER = getLogger(Slf4JBridge.class);

	@Override
	public void trace(Object obj)
	{
		LOGGER.trace(obj.toString());
	}

	@Override
	public void debug(Object obj)
	{
		LOGGER.debug(obj.toString());
	}

	@Override
	public void info(Object obj)
	{
		LOGGER.info(obj.toString());
	}

	@Override
	public void warning(Object obj)
	{
		LOGGER.warn(obj.toString());
	}

	@Override
	public void error(Object obj)
	{
		LOGGER.error(obj.toString());
	}

	@Override
	public void error(Throwable e)
	{
		LOGGER.error("", e);
	}

	@Override
	public void error(Object obj, Throwable e)
	{
		LOGGER.error(obj.toString(), e);
	}

	@Override
	public void fatal(Object obj)
	{
		LOGGER.error(obj.toString());
	}
}
