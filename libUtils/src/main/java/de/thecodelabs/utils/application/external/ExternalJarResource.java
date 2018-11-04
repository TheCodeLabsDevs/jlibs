package de.thecodelabs.utils.application.external;

import de.thecodelabs.utils.application.AbstractResource;

import java.io.InputStream;

public class ExternalJarResource extends AbstractResource
{
	private final InputStream inputStream;

	ExternalJarResource(InputStream inputStream)
	{
		this.inputStream = inputStream;
	}

	@Override
	public InputStream getInputStream()
	{
		return inputStream;
	}
}
