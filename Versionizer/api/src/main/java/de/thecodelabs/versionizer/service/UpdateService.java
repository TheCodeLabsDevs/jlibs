package de.thecodelabs.versionizer.service;

import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.impl.AppVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.ExeVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.JarVersionizerStrategy;
import de.thecodelabs.versionizer.service.impl.VersionizerStrategy;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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

	public enum RunPrivilages {
		ADMIN,
		USER
	}

	private VersionizerItem versionizerItem;
	private VersionizerStrategy updateStrategy;
	private VersionService versionService;

	private InteractionType interactionType;
	private RunPrivilages runPrivilages;

	private Map<Artifact, Version> remoteVersions;

	private UpdateService(VersionizerItem item, Strategy strategy, InteractionType interactionType)
	{
		this.remoteVersions = new HashMap<>();

		this.versionizerItem = item;
		this.interactionType = interactionType;

		if (Files.isWritable(Paths.get(versionizerItem.getExecutablePath()))) {
			this.runPrivilages = RunPrivilages.USER;
		} else {
			this.runPrivilages = RunPrivilages.ADMIN;
		}

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
		UpdateService updateService = new UpdateService(versionizerItem, strategy, interactionType);
		try
		{
			updateService.updateStrategy.downloadVersionizer(interactionType);
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		return updateService;
	}

	public void fetchCurrentVersion()
	{
		for(Artifact artifact : versionizerItem.getArtifacts())
		{
			remoteVersions.put(artifact, versionService.getLatestVersion(artifact));
		}
	}

	public boolean isUpdateAvailable() {
		if (remoteVersions.isEmpty()) {
			fetchCurrentVersion();
		}

		for(Artifact artifact : versionizerItem.getArtifacts())
		{
			if (remoteVersions.containsKey(artifact)) {
				final Version localVersion = VersionTokenizer.getVersion(artifact);
				boolean remoteNewer = remoteVersions.get(artifact).isNewerThen(localVersion);
				if (remoteNewer) {
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

	public void updateArtifacts()
	{
		for(Artifact artifact : versionizerItem.getArtifacts())
		{
			System.out.println("Update " + artifact);
		}
	}

	public void runVersionizerInstance() {
		Path path  = updateStrategy.getUpdaterPath(interactionType);
	}
}
