package de.thecodelabs.versionizer;

public class VersionizerItem
{
	private String artifactoryUrl;
	private String releaseRepository;
	private String snapshotRepository;

	private String groupId;
	private String artifactId;

	private String executablePath;

	public String getArtifactoryUrl()
	{
		return artifactoryUrl;
	}

	public void setArtifactoryUrl(String artifactoryUrl)
	{
		this.artifactoryUrl = artifactoryUrl;
	}

	public String getReleaseRepository()
	{
		return releaseRepository;
	}

	public void setReleaseRepository(String releaseRepository)
	{
		this.releaseRepository = releaseRepository;
	}

	public String getSnapshotRepository()
	{
		return snapshotRepository;
	}

	public void setSnapshotRepository(String snapshotRepository)
	{
		this.snapshotRepository = snapshotRepository;
	}

	public String getRepository(boolean snapshot) {
		if (snapshot) {
			return snapshotRepository;
		} else {
			return releaseRepository;
		}
	}

	public String getGroupId()
	{
		return groupId;
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	public String getArtifactId()
	{
		return artifactId;
	}

	public void setArtifactId(String artifactId)
	{
		this.artifactId = artifactId;
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
