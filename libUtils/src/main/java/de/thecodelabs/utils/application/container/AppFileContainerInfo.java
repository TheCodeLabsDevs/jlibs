package de.thecodelabs.utils.application.container;

import de.thecodelabs.storage.settings.annotation.Key;
import de.thecodelabs.storage.settings.annotation.Required;

import java.util.ArrayList;
import java.util.List;

public class AppFileContainerInfo
{

	@Key
	@Required
	private String identifier;
	@Key
	@Required
	private String name;
	@Key
	@Required
	private long build;

	@Key
	private String executionPath;
	@Key
	private String updatePath;

	@Key
	private List<BackupInfo> backups = new ArrayList<>();

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the build
	 */
	public long getBuild() {
		return build;
	}

	public List<BackupInfo> getBackups() {
		return backups;
	}

	public String getExecutionPath() {
		return executionPath;
	}

	public String getUpdatePath() {
		return updatePath;
	}

	/**
	 * @param identifier the identifier to set
	 */
	void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param name the name to set
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * @param build the build to set
	 */
	void setBuild(long build) {
		this.build = build;
	}

	void setExecutionPath(String executionPath) {
		this.executionPath = executionPath;
	}

	void setUpdatePath(String updatePath) {
		this.updatePath = updatePath;
	}

}
