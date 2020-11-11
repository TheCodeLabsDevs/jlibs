package de.thecodelabs.utils.application.resources.classpath;

import de.thecodelabs.utils.util.StringUtils;

public class ClasspathResourceContainer
{
	public ClasspathResource get(String... name)
	{
		final String child = StringUtils.build(name, "/");
		final Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
		return new ClasspathResource(callerClass, child);
	}
}
