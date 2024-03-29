package de.thecodelabs.utils.application.container;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationInfo;
import de.thecodelabs.utils.io.FileUtils;
import de.thecodelabs.utils.io.PathUtils;
import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.SystemUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AppFileContainer
{
	private static final String CONTAINER_YML = "container.yml";

	private final AppFileContainerInfo containerInfo;
	private String containerName = "Java File Container";
	private Path containerPath;
	private Path containerInfoPath;

	private ApplicationInfo info;
	private App app;

	public AppFileContainer(App app)
	{
		this.app = app;
		this.info = app.getInfo();
		if(app.getInfo().getBasePath() != null && !app.getInfo().getBasePath().isEmpty())
		{
			this.containerName = app.getInfo().getBasePath();
		}

		updatePath();

		if(Files.exists(containerInfoPath))
		{
			containerInfo = Storage.load(containerInfoPath, StorageTypes.YAML, AppFileContainerInfo.class);
		}
		else
		{
			containerInfo = new AppFileContainerInfo();
			saveInformation();
		}

		PathUtils.createDirectoriesIfNotExists(containerPath);
	}

	public AppFileContainer(String bundleIdentifier)
	{
		containerPath = SystemUtils.getApplicationSupportDirectoryPath(containerName, bundleIdentifier);
		containerInfoPath = getPath(CONTAINER_YML, PathType.ROOT);
		containerInfo = Storage.load(containerInfoPath, StorageTypes.YAML, AppFileContainerInfo.class);
	}

	public void updatePath()
	{
		if(app.isDebug())
		{
			containerPath = SystemUtils.getApplicationSupportDirectoryPath(containerName, info.getIdentifier() + ".debug");
		}
		else
		{
			containerPath = SystemUtils.getApplicationSupportDirectoryPath(containerName, info.getIdentifier());
		}
		containerInfoPath = getPath("container.yml", PathType.ROOT);
	}

	public Path getPath(String name, ContainerPathType pathType)
	{
		final Path baseFolder = containerPath.resolve(pathType.getFolder());
		if(Files.notExists(baseFolder))
		{
			try
			{
				Files.createDirectories(baseFolder);
			}
			catch(IOException e)
			{
				throw new UncheckedIOException(e);
			}
		}
		final Path path = baseFolder.resolve(name);
		if(Files.notExists(path.getParent()))
		{
			try
			{
				Files.createDirectories(path.getParent());
				LoggerBridge.debug("Create directory at '" + path.getParent().toString() + "'");
			}
			catch(IOException e)
			{
				throw new UncheckedIOException(e);
			}
		}
		return path;
	}

	public Path getFolder(ContainerPathType type)
	{
		return containerPath.resolve(type.getFolder());
	}

	public Path getContainerPath()
	{
		return containerPath;
	}

	public AppFileContainerInfo getContainerInfo()
	{
		return containerInfo;
	}

	public void clear() throws IOException
	{
		FileUtils.deleteDirectory(getContainerPath());
	}


	public void saveInformation()
	{
		// Update der Informationen
		try
		{
			final Path runPath = SystemUtils.getRunPath();
			if(runPath != null)
			{
				containerInfo.setExecutionPath(runPath.toFile().getAbsolutePath());
			}
		}
		catch(UnsupportedOperationException e) // Not working in jlink env
		{
			LoggerBridge.info("Cannot get run path");
		}

		if(info.getUpdateURL() != null)
		{
			containerInfo.setUpdatePath(info.getUpdateURL());
		}

		containerInfo.setBuild(info.getBuild());
		containerInfo.setIdentifier(info.getIdentifier());
		containerInfo.setName(info.getName());

		Storage.save(containerInfoPath, StorageTypes.YAML, containerInfo);
	}
}
