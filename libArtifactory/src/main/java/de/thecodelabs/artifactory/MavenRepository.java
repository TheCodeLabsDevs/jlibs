package de.thecodelabs.artifactory;

import de.thecodelabs.artifactory.net.JsonBodyHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class MavenRepository
{
	private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
			.followRedirects(HttpClient.Redirect.NORMAL).proxy(ProxySelector.getDefault()).build();

	private Artifactory artifactory;
	private String name;

	public MavenRepository(Artifactory artifactory, String name)
	{
		this.artifactory = artifactory;
		this.name = name;
	}

	public Folder getFolder(String path)
	{
		String url = String.format("%s/api/storage/%s/%s", artifactory.getUrl(), name, path);
		try
		{
			final HttpRequest request = HttpRequest
					.newBuilder()
					.uri(URI.create(url))
					.GET()
					.build();
			return HTTP_CLIENT.send(request, new JsonBodyHandler<>(Folder.class))
					.body()
					.get();
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	public File getFile(String path)
	{
		String url = String.format("%s/api/storage/%s/%s", artifactory.getUrl(), name, path);

		try
		{
			final HttpRequest request = HttpRequest
					.newBuilder()
					.uri(URI.create(url))
					.GET()
					.build();
			return HTTP_CLIENT.send(request, new JsonBodyHandler<>(File.class))
					.body()
					.get();
		}
		catch(IOException e)
		{
			throw new UncheckedIOException(e);
		}
		catch(InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	public InputStream download(String path) throws IOException
	{
		return new URL(getFile(path).getDownloadUri()).openStream();
	}
}
