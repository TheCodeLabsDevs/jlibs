package de.thecodelabs.utils.application.container;

public enum PathType {

	RESOURCES("Resources", true),
	@Deprecated
	HELPMAP("Resources/HelpMap", false),
	DOCUMENTS("Documents", true),
	CONFIGURATION("Config", true),
	LIBRARY("Library", true),
	NATIVE_LIBRARY("Library/Native", false),
	ROOT("", false),
	BACKUP("Backup", false),
	STORE("Store", true),
	CACHE("Cache", false),
	DOWNLOAD("Download", false),
	LOG("Log", false);

	private String folder;
	private boolean backup;
	private boolean sync;

	PathType(String folder, boolean backup) {
		this.folder = folder;
		this.backup = backup;
	}

	public String getFolder() {
		return folder;
	}

	/**
	 * @return the backup
	 */
	public boolean shouldBackup() {
		return backup;
	}

}
