package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.config.Repository;

import java.util.Collections;
import java.util.List;

public class VersionizerItem
{
	private Repository repository;
	private List<Artifact> artifactList;

	private String executablePath;

	public VersionizerItem(Repository repository, List<Artifact> buildList, String executablePath)
	{
		this.repository = repository;
		this.artifactList = buildList;
		this.executablePath = executablePath;
	}

	public VersionizerItem(Repository repository, Artifact artifact, String executablePath)
	{
		this(repository, Collections.singletonList(artifact), executablePath);
	}

	public Repository getRepository()
	{
		return repository;
	}

	public List<Artifact> getArtifacts()
	{
		return artifactList;
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
