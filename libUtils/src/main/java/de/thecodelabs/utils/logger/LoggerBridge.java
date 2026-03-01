package de.thecodelabs.utils.logger;

public abstract class LoggerBridge
{
	private static LogInterface implementation = new LibLoggerBridge();

	public static LogInterface getImplementation()
	{
		return implementation;
	}

	public static void setImplementation(LogInterface implementation)
	{
		LoggerBridge.implementation = implementation;
	}

	public static void trace(Object obj)
	{
		if(implementation != null)
		{
			implementation.trace(obj);
		}
		else
		{
			System.out.println(obj);
		}
	}

	public static void debug(Object obj)
	{
		if(implementation != null)
		{
			implementation.debug(obj);
		}
		else
		{
			System.out.println(obj);
		}
	}

	public static void info(Object obj)
	{
		if(implementation != null)
		{
			implementation.info(obj);
		}
		else
		{
			System.out.println(obj);
		}
	}

	public static void warning(Object obj)
	{
		if(implementation != null)
		{
			implementation.warning(obj);
		}
		else
		{
			System.out.println(obj);
		}
	}

	public static void error(Object obj)
	{
		if(implementation != null)
		{
			implementation.error(obj);
		}
		else
		{
			System.err.println(obj);
		}
	}

	protected abstract void errorImpl(Object obj);

	public static void error(Throwable e)
	{
		if(implementation != null)
		{
			implementation.error(e);
		}
		else
		{
			e.printStackTrace();
		}
	}

	protected abstract void errorImpl(Throwable e);

	public static void error(Object obj, Throwable e)
	{
		if(implementation != null)
		{
			implementation.error(obj, e);
		}
		else
		{
			e.printStackTrace();
		}
	}

	protected abstract void errorImpl(Object obj, Throwable e);

	public static void fatal(Object obj)
	{
		if(implementation != null)
		{
			implementation.fatal(obj);
		}
		else
		{
			System.err.println(obj);
		}
	}

	protected abstract void fatalImpl(Object obj);
}
