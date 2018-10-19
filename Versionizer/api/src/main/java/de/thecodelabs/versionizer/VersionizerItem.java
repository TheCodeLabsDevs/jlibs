package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.config.Build;
import de.thecodelabs.versionizer.config.Repository;

import java.util.List;

public class VersionizerItem
{
	private Repository repository;
	private List<Build> buildList;

	private String executablePath;

	public VersionizerItem(Repository repository, List<Build> buildList, String executablePath)
	{
		this.repository = repository;
		this.buildList = buildList;
		this.executablePath = executablePath;
	}

	public Repository getRepository()
	{
		return repository;
	}

	public List<Build> getBuildList()
	{
		return buildList;
	}

	public String getExecutablePath()
	{
		return executablePath;
	}

	public void setExecutablePath(String executablePath)
	{
		this.executablePath = executablePath;
	}
}
