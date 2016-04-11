package de.tobias.utils.application.container;

public enum PathType {

	RESOURCES("Resources", true),
	HELPMAP("Resources/HelpMap", false),
	DOCUMENTS("Documents", true),
	CONFIGURATION("Config", true),
	LIBRARY("Library", true),
	NATIVELIBRARY("Library/Native", false),
	ROOT("", false),
	BACKUP("Backup", false),
	STORE("Store", true),
	CACHE("Cache", false),
	DOWNLOAD("Download", false),
	LOG("Log", false);

	private String folder;
	private boolean backup;

	private PathType(String folder, boolean backup) {
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
