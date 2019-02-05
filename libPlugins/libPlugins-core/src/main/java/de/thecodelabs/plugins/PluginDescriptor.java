package de.thecodelabs.plugins;

import de.thecodelabs.storage.settings.annotation.Key;

public class PluginDescriptor
{
	@Key
	private String main;
	@Key
	private String name;
	@Key
	private String artifactId;
	@Key
	private String groupId;
	@Key
	private String version;
	@Key
	private long build;

	public String getMain()
	{
		return main;
	}

	public String getName()
	{
		return name;
	}

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

	public long getBuild()
	{
		return build;
	}
}
