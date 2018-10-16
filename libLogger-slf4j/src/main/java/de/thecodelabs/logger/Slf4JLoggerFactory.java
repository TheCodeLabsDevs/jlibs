package de.thecodelabs.logger;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class Slf4JLoggerFactory implements ILoggerFactory
{
	private Slf4JLoggerAdapter instance;

	public Logger getLogger(String s)
	{
		if(instance == null)
		{
			instance = new Slf4JLoggerAdapter();
		}
		return instance;
	}
}
