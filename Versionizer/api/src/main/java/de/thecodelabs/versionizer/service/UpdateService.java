package de.thecodelabs.versionizer.service;

import de.thecodelabs.versionizer.UpdateItem;
import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.impl.AppVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.ExeVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.JarVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.VersionizerStrategy;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateService
{
	public enum Strategy
	{
		JAR,
		EXE,
		APP
	}

	public enum InteractionType
	{
		GUI,
		HEADLESS
	}

	public enum RunPrivileges
	{
		ADMIN,
		USER
	}

	private VersionizerItem versionizerItem;
	private VersionizerStrategy updateStrategy;
	private VersionService versionService;

	private InteractionType interactionType;
	private RunPrivileges runPrivileges;

	private Map<Artifact, Version> remoteVersions;

	private UpdateService(VersionizerItem item, Strategy strategy, InteractionType interactionType)
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
		this.versionService = new VersionService(versionizerItem);
	}

	public static UpdateService startVersionizer(VersionizerItem versionizerItem, Strategy strategy, InteractionType interactionType)
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
		return new UpdateService(versionizerItem, strategy, interactionType);
	}

	public void fetchCurrentVersion()
	{
		for(Artifact artifact : versionizerItem.getArtifacts())
		{
			remoteVersions.put(artifact, versionService.getLatestVersion(artifact));
		}
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
				boolean remoteNewer = remoteVersion.isNewerThen(localVersion);
				if(remoteNewer)
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

	public void runVersionizerInstance(List<UpdateItem.Entry> versions) throws IOException
	{
		updateStrategy.downloadVersionizer(interactionType);

		final boolean anyAdmin = versions.stream().anyMatch(entry -> !Files.isWritable(Paths.get(entry.getLocalPath())));
		if(anyAdmin)
		{
			this.runPrivileges = RunPrivileges.USER;
		}
		else
		{
			this.runPrivileges = RunPrivileges.ADMIN;
		}

		updateStrategy.startVersionizer(interactionType, runPrivileges, new UpdateItem(versionizerItem, versions));
	}
}
