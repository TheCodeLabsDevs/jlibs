package de.thecodelabs.utils.application.external;

import de.thecodelabs.utils.util.StringUtils;
import de.thecodelabs.utils.util.zip.ZipFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExternalClasspathResourceContainer
{
	private static Map<Path, ExternalClasspathResourceContainer> pathExternalClasspathResourceContainerMap;

	static
	{
		pathExternalClasspathResourceContainerMap = new HashMap<>();
	}

	public static ExternalClasspathResourceContainer getExternalClasspath(Path archiveFile)
	{
		if(pathExternalClasspathResourceContainerMap.containsKey(archiveFile))
		{
			return pathExternalClasspathResourceContainerMap.get(archiveFile);
		}
		try
		{
			final ZipFile zipFile = new ZipFile(archiveFile, ZipFile.FileMode.READ);
			ExternalClasspathResourceContainer container = new ExternalClasspathResourceContainer(archiveFile, zipFile);
			pathExternalClasspathResourceContainerMap.put(archiveFile, container);
			return container;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private final Path archiveFile;
	private final ZipFile zipFile;

	public ExternalClasspathResourceContainer(Path archiveFile, ZipFile zipFile)
	{
		this.archiveFile = archiveFile;
		this.zipFile = zipFile;
	}

	public ExternalClasspathResource get(String... name) {
		final String path = StringUtils.build(name, "/");
		try
		{
			return new ExternalClasspathResource(zipFile.inputStream(path));
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void close()
	{
		try
		{
			zipFile.close();
			ExternalClasspathResourceContainer.pathExternalClasspathResourceContainerMap.remove(archiveFile);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
