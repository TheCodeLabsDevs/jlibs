package de.thecodelabs.utils.application.resources.classpath;

import de.thecodelabs.utils.application.AbstractResource;

import java.io.InputStream;

public class ClasspathResource extends AbstractResource
{
	private final Class<?> callerClass;
	private final String name;

	ClasspathResource(Class<?> callerClass, String name)
	{
		this.callerClass = callerClass;
		this.name = name;
	}

	@Override
	public InputStream getInputStream()
	{
		return callerClass.getClassLoader().getResourceAsStream(name);
	}
}
