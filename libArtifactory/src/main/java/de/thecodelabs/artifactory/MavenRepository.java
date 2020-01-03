package de.thecodelabs.artifactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class MavenRepository
{
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
		final HttpResponse<String> folderResponse = Unirest.get(url).asString();

		ObjectMapper mapper = new ObjectMapper();
		try
		{
			return mapper.readValue(folderResponse.getBody(), Folder.class);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public File getFile(String path)
	{
		String url = String.format("%s/api/storage/%s/%s", artifactory.getUrl(), name, path);
		final HttpResponse<String> folderResponse = Unirest.get(url).asString();

		ObjectMapper mapper = new ObjectMapper();
		try
		{
			return mapper.readValue(folderResponse.getBody(), File.class);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public InputStream download(String path) throws IOException
	{
		return new URL(getFile(path).getDownloadUri()).openStream();
	}
}
