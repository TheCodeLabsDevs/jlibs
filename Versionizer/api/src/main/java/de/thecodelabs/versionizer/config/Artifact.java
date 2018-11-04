package de.thecodelabs.versionizer.config;

import de.thecodelabs.storage.settings.annotation.Classpath;
import de.thecodelabs.storage.settings.annotation.Key;

@Classpath("build.properties")
public class Artifact
{
	@Key("build.artifactId")
	private String artifactId;
	@Key("build.groupId")
	private String groupId;
	@Key("build.version")
	private String version;

	public String getArtifactId()
	{
		return artifactId;
	}

	public String getGroupId()
	{
		return groupId.replace(".", "/");
	}

	public String getVersion()
	{
		return version;
	}

	public void setArtifactId(String artifactId)
	{
		this.artifactId = artifactId;
	}

	public void setGroupId(String groupId)
	{
		this.groupId = groupId;
	}

	public void setVersion(String version)
	{
		this.version = version;
	}

	@Override
	public String toString()
	{
		return "Artifact{" +
				"artifactId='" + artifactId + '\'' +
				", groupId='" + groupId + '\'' +
				", version='" + version + '\'' +
				'}';
	}


}
