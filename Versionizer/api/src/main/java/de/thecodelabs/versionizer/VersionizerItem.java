package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.config.Repository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VersionizerItem
{
	private Repository repository;
	private List<Artifact> artifactList;

	private String executablePath;

	public VersionizerItem(Repository repository, String executablePath)
	{
		this.repository = repository;
		this.artifactList = new ArrayList<>();
		this.executablePath = executablePath;
	}

	public Repository getRepository()
	{
		return repository;
	}

	public List<Artifact> getArtifacts()
	{
		return artifactList;
	}

	public void addArtifact(Artifact artifact, Path localPath)
	{
		if(!artifactList.contains(artifact))
		{
			artifact.setLocalPath(localPath);
			artifactList.add(artifact);
		}
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
