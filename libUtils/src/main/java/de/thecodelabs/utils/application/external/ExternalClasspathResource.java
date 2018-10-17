package de.thecodelabs.utils.application.external;

import de.thecodelabs.utils.application.AbstractResource;

import java.io.InputStream;

public class ExternalClasspathResource extends AbstractResource
{
	private final InputStream inputStream;

	ExternalClasspathResource(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}

	@Override
	public InputStream getInputStream()
	{
		return inputStream;
	}
}
