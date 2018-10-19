package de.thecodelabs.versionizer.config;

import de.thecodelabs.storage.settings.annotation.Classpath;
import de.thecodelabs.storage.settings.annotation.Key;

@Classpath("build.properties")
public class Build
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
		return groupId;
	}

	public String getVersion()
	{
		return version;
	}

	@Override
	public String toString()
	{
		return "Build{" +
				"artifactId='" + artifactId + '\'' +
				", groupId='" + groupId + '\'' +
				", version='" + version + '\'' +
				'}';
	}
}
