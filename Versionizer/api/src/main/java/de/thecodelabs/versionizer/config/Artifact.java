package de.thecodelabs.versionizer.config;

import de.thecodelabs.storage.settings.annotation.Classpath;
import de.thecodelabs.storage.settings.annotation.Key;
import de.thecodelabs.storage.settings.annotation.Required;

import java.nio.file.Path;
import java.util.Objects;

@Classpath("build.json")
public class Artifact
{
	public enum ArtifactType
	{
		RUNTIME,
		PLUGIN
	}

	@Key("build.artifactId")
	@Required
	private String artifactId;
	@Key("build.groupId")
	@Required
	private String groupId;
	@Key("build.version")
	@Required
	private String version;
	@Key("build.type")
	private ArtifactType artifactType = ArtifactType.RUNTIME;

	private Path localPath;

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

	public ArtifactType getArtifactType()
	{
		return artifactType;
	}

	public void setArtifactType(ArtifactType artifactType)
	{
		this.artifactType = artifactType;
	}

	public Path getLocalPath()
	{
		return localPath;
	}

	public void setLocalPath(Path localPath)
	{
		this.localPath = localPath;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Artifact)) return false;
		Artifact artifact = (Artifact) o;
		return Objects.equals(artifactId, artifact.artifactId) &&
				Objects.equals(groupId, artifact.groupId) &&
				Objects.equals(version, artifact.version) &&
				artifactType == artifact.artifactType;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(artifactId, groupId, version, artifactType);
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
