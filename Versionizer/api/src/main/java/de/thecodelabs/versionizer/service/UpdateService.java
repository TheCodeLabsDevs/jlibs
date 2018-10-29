package de.thecodelabs.versionizer.service;

import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.service.impl.AppUpdateStrategy;
import de.thecodelabs.versionizer.service.impl.ExeUpdateStrategy;
import de.thecodelabs.versionizer.service.impl.JarUpdateStrategy;
import de.thecodelabs.versionizer.service.impl.UpdateStrategy;

import java.io.IOException;

public class UpdateService
{
	public enum Strategy
	{
		JAR,
		EXE,
		APP
	}

	private VersionizerItem versionizerItem;
	private UpdateStrategy updateStrategy;

	private UpdateService(VersionizerItem item, Strategy strategy)
	{
		this.versionizerItem = item;
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

	public static UpdateService startVersionizer(VersionizerItem versionizerItem, Strategy strategy)
	{
		UpdateService updateService = new UpdateService(versionizerItem, strategy);
		try
		{
			updateService.updateStrategy.downloadVersionizer();
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
		return updateService;
	}

	public boolean isUpdateAvailable()
	{
		for(Artifact artifact : versionizerItem.getArtifacts())
		{
			final boolean response = updateStrategy.isUpdateAvailableForArtifact(versionizerItem.getRepository(), artifact);
			if(response)
			{
				return true;
			}
		}
		return false;
	}

	public void updateArtifacts()
	{
		for(Artifact artifact : versionizerItem.getArtifacts())
		{
			System.out.println("Update " + artifact);
		}
	}

}
