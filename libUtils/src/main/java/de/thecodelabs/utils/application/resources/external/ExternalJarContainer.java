package de.thecodelabs.utils.application.resources.external;

import de.thecodelabs.utils.util.StringUtils;
import de.thecodelabs.utils.util.zip.ZipFile;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class ExternalJarContainer
{
	private static Map<Path, ExternalJarContainer> pathExternalJarContainerMap;

	static
	{
		pathExternalJarContainerMap = new HashMap<>();
	}

	public static ExternalJarContainer getExternalJar(Path archiveFile)
	{
		if(pathExternalJarContainerMap.containsKey(archiveFile))
		{
			return pathExternalJarContainerMap.get(archiveFile);
		}
		try
		{
			final ZipFile zipFile = new ZipFile(archiveFile, ZipFile.FileMode.READ);
			ExternalJarContainer container = new ExternalJarContainer(archiveFile, zipFile);
			pathExternalJarContainerMap.put(archiveFile, container);
			return container;
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private final Path archiveFile;
	private final ZipFile zipFile;

	private ExternalJarContainer(Path archiveFile, ZipFile zipFile)
	{
		this.archiveFile = archiveFile;
		this.zipFile = zipFile;
	}

	public ExternalJarResource get(String... name) {
		final String path = StringUtils.build(name, "/");
		try
		{
			return new ExternalJarResource(zipFile.inputStream(path));
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
			ExternalJarContainer.pathExternalJarContainerMap.remove(archiveFile);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public Path getArchiveFile()
	{
		return archiveFile;
	}

	public ZipFile getZipFile()
	{
		return zipFile;
	}
}
