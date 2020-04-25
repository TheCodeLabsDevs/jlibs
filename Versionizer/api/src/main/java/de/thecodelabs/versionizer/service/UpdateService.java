package de.thecodelabs.versionizer.service;

import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.SystemUtils;
import de.thecodelabs.versionizer.UpdateItem;
import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.impl.AppVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.ExeVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.JarVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.VersionizerStrategy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UpdateService
{
	public enum Strategy
	{
		JAR,
		EXE,
		APP,
		JLINK
	}

	public enum InteractionType
	{
		GUI,
		HEADLESS
	}

	public enum RepositoryType
	{
		RELEASE,
		SNAPSHOT,
		ALL
	}

	public enum RunPrivileges
	{
		ADMIN,
		USER
	}

	private VersionizerItem versionizerItem;
	private VersionService versionService;

	private VersionizerStrategy updateStrategy;
	private InteractionType interactionType;

	private Map<Artifact, Version> remoteVersions;

	private UpdateService(VersionizerItem item, Strategy strategy, InteractionType interactionType, RepositoryType repositoryType)
	{
		this.remoteVersions = new HashMap<>();

		this.versionizerItem = item;
		this.interactionType = interactionType;

		switch(strategy)
		{
			case JAR:
				updateStrategy = new JarVersionizerStrategy();
				break;
			case EXE:
				updateStrategy = new ExeVersionizerStrategy();
				break;
			case APP:
				updateStrategy = new AppVersionizerStrategy();
				break;
		}
		this.versionService = new VersionService(versionizerItem.getRepository(), repositoryType);
	}

	public static UpdateService startVersionizer(VersionizerItem versionizerItem, Strategy strategy, InteractionType interactionType)
	{
		return startVersionizer(versionizerItem, strategy, interactionType, RepositoryType.RELEASE);
	}

	public static UpdateService startVersionizer(VersionizerItem versionizerItem, Strategy strategy, InteractionType interactionType, RepositoryType repositoryType)
	{
		try
		{
			final Class<?> slf4JLoggerAdapter = Class.forName("de.thecodelabs.logger.Slf4JLoggerAdapter");
			final Method disableSlf4jDebugPrints = slf4JLoggerAdapter.getMethod("disableSlf4jDebugPrints");
			disableSlf4jDebugPrints.invoke(null);
		}
		catch(ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored)
		{
		}
		return new UpdateService(versionizerItem, strategy, interactionType, repositoryType);
	}

	public void fetchCurrentVersion()
	{
		remoteVersions.clear();
		versionizerItem.getArtifacts().forEach(artifact -> remoteVersions.put(artifact, versionService.getLatestVersion(artifact)));
	}

	public boolean isUpdateAvailable()
	{
		if(remoteVersions.isEmpty())
		{
			fetchCurrentVersion();
		}

		for(Artifact artifact : versionizerItem.getArtifacts())
		{
			if(remoteVersions.containsKey(artifact))
			{
				final Version localVersion = VersionTokenizer.getVersion(artifact);
				final Version remoteVersion = remoteVersions.get(artifact);
				if(remoteVersion.isNewerThen(localVersion))
				{
					return true;
				}
			}
		}
		return false;
	}

	public Map<Artifact, Version> getRemoteVersions()
	{
		return remoteVersions;
	}

	public Version getRemoteVersionForArtifact(Artifact artifact)
	{
		return remoteVersions.get(artifact);
	}

	public void runVersionizerInstance(UpdateItem.Entry version) throws IOException
	{
		runVersionizerInstance(Collections.singletonList(version));
	}

	public void runVersionizerInstance(List<UpdateItem.Entry> entries) throws IOException
	{
		LoggerBridge.debug("Start checking versionizer");
		updateStrategy.downloadVersionizer(interactionType);
		RunPrivileges runPrivileges = getRunPrivileges(entries);
		LoggerBridge.debug("Versionizer run privilege: " + runPrivileges);
		prepareFileTypes(entries);
		LoggerBridge.debug("Start running versionizer");
		updateStrategy.startVersionizer(interactionType, runPrivileges, new UpdateItem(versionizerItem, entries));
	}

	private RunPrivileges getRunPrivileges(List<UpdateItem.Entry> entries)
	{
		final boolean anyAdmin = entries.stream()
				.map(entry -> entry.getVersion().getArtifact().getLocalPath())
				.anyMatch(path -> !Files.isWritable(path));

		RunPrivileges runPrivileges;
		if(anyAdmin)
		{
			runPrivileges = RunPrivileges.USER;
		}
		else
		{
			runPrivileges = RunPrivileges.ADMIN;
		}
		return runPrivileges;
	}

	private void prepareFileTypes(List<UpdateItem.Entry> entries)
	{
		for(UpdateItem.Entry entry : entries)
		{
			if(entry.getFileType() == null)
			{
				switch(entry.getVersion().getArtifact().getArtifactType())
				{
					case RUNTIME:
						if(SystemUtils.isJar())
						{
							entry.setFileType(RemoteFile.FileType.JAR);
						}
						else if(SystemUtils.isExe())
						{
							entry.setFileType(RemoteFile.FileType.EXE);
						}
						break;
					case PLUGIN:
						entry.setFileType(RemoteFile.FileType.JAR);
				}
			}
		}
	}

	public void addArtifact(Artifact artifact, Path localPath)
	{
		this.versionizerItem.addArtifact(artifact, localPath);
	}

	public void removeArtifact(Artifact artifact)
	{
		this.versionizerItem.getArtifacts().remove(artifact);
	}

	public List<Artifact> getArtifacts()
	{
		return this.versionizerItem.getArtifacts();
	}

	public List<UpdateItem.Entry> getAllLatestVersionEntries()
	{
		return getArtifacts().stream()
				.map(artifact -> new UpdateItem.Entry(getRemoteVersionForArtifact(artifact)))
				.collect(Collectors.toList());
	}

	public void setRepositoryType(RepositoryType repositoryType)
	{
		this.versionService.setRepositoryType(repositoryType);
	}
}
