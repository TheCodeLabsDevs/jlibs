package de.thecodelabs.utils.application.container;

public enum PathType implements ContainerPathType
{
	ROOT("", false),


	RESOURCES("Resources", true),
	DOCUMENTS("Documents", true),
	CONFIGURATION("Config", true),

	LIBRARY("Library", true),
	NATIVE_LIBRARY("Library/Native", false),

	BACKUP("Backup", false),
	CACHE("Cache", false),
	DOWNLOAD("Download", false),
	LOG("Log", false),

	@Deprecated
	STORE("Store", true),
	@Deprecated
	HELPMAP("Resources/HelpMap", false);

	private String folder;
	private boolean backup;

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
