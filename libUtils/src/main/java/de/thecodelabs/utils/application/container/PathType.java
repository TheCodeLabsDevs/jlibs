package de.thecodelabs.utils.application.container;

public enum PathType implements ContainerPathType
{
	ROOT("", false),

	RESOURCES("Resources", true),
	DOCUMENTS("Documents", true),
	CONFIGURATION("Config", true),

	LIBRARY("Library", true),
	NATIVE_LIBRARY("Library/Native", false),

	CACHE("Cache", false),
	DOWNLOAD("Download", false),
	LOG("Log", false);

	private final String folder;
	private final boolean backup;

	PathType(String folder, boolean backup)
	{
		this.folder = folder;
		this.backup = backup;
	}

	@Override
	public String getFolder()
	{
		return folder;
	}

	@Override
	public boolean shouldBackup()
	{
		return backup;
	}
}
