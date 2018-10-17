package de.thecodelabs.utils.application.remote;

import de.thecodelabs.utils.application.AbstractResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class RemoteResource extends AbstractResource
{
	private URLConnection urlConnection;

	RemoteResource(URLConnection urlConnection)
	{
		this.urlConnection = urlConnection;
	}

	@Override
	public InputStream getInputStream()
	{
		try
		{
			return urlConnection.getInputStream();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
