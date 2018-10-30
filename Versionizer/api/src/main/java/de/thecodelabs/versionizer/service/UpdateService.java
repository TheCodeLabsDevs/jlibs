package de.thecodelabs.versionizer.service;

import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.impl.AppUpdateStrategy;
import de.thecodelabs.versionizer.service.impl.ExeUpdateStrategy;
import de.thecodelabs.versionizer.service.impl.JarUpdateStrategy;
import de.thecodelabs.versionizer.service.impl.UpdateStrategy;

import java.io.IOException;
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

	private VersionizerItem versionizerItem;
	private UpdateStrategy updateStrategy;
	private InteractionType interactionType;

	private Map<Artifact, Version> remoteVersions;

	private UpdateService(VersionizerItem item, Strategy strategy, InteractionType interactionType)
	{
		this.remoteVersions = new HashMap<>();

		this.versionizerItem = item;
		this.interactionType = interactionType;
		switch(strategy)
		{
			case JAR:
				updateStrategy = new JarUpdateStrategy();
				break;
			case EXE:
				updateStrategy = new ExeUpdateStrategy();
				break;
			case APP:
				updateStrategy = new AppUpdateStrategy();
				break;
		}
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
			remoteVersions.put(artifact, updateStrategy.fetchCurrentVersion(versionizerItem.getRepository(), artifact));
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

}
