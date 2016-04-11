package de.tobias.utils.application.container;

import java.util.ArrayList;
import java.util.List;

import de.tobias.utils.settings.Required;
import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.Storable;

public class FileContainerInfo implements SettingsSerializable {

	private static final long serialVersionUID = 1L;

	@Storable @Required private String identifier;
	@Storable @Required private String name;
	@Storable @Required private long build;
	
	@Storable private String executionPath;
	@Storable private String updatePath;
	
	@Storable private List<BackupInfo> backups = new ArrayList<>();

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
	 * @param identifier
	 *            the identifier to set
	 */
	void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * @param build
	 *            the build to set
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
