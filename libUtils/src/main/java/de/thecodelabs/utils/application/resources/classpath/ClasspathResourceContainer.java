package de.thecodelabs.utils.application.resources.classpath;

import de.thecodelabs.utils.util.StringUtils;

public class ClasspathResourceContainer
{
	public ClasspathResource get(String... name)
	{
		final String child = StringUtils.build(name, "/");
		try
		{
			final Class<?> callerClass = Class.forName(getCallerClassName());
			return new ClasspathResource(callerClass, child);
		}
		catch(ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}
	}

	private static String getCallerClassName()
	{
		StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
		for(int i = 1; i < stElements.length; i++)
		{
			StackTraceElement ste = stElements[i];
			if(!ste.getClassName().equals(ClasspathResourceContainer.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0)
			{
				return ste.getClassName();
			}
		}
		return null;
	}
}
