package de.thecodelabs.utils.application.container;

public enum PathType {

	RESOURCES("Resources", true, false),
	@Deprecated
	HELPMAP("Resources/HelpMap", false, false),
	DOCUMENTS("Documents", true, true),
	CONFIGURATION("Config", true, true),
	LIBRARY("Library", true, false),
	@Deprecated
	NATIVELIBRARY("Library/Native", false, false),
	NATIVE_LIBRARY("Library/Native", false, false),
	ROOT("", false, false),
	BACKUP("Backup", false, false),
	STORE("Store", true, false),
	CACHE("Cache", false, false),
	DOWNLOAD("Download", false, false),
	LOG("Log", false, false);

	private String folder;
	private boolean backup;
	private boolean sync;

	PathType(String folder, boolean backup, boolean sync) {
		this.folder = folder;
		this.backup = backup;
		this.sync = sync;
	}

	public String getFolder() {
		return folder;
	}

	public boolean isSync() {
		return sync;
	}

	/**
	 * @return the backup
	 */
	public boolean shouldBackup() {
		return backup;
	}

}
