package de.tobias.utils.application;

import de.tobias.utils.settings.Key;
import de.tobias.utils.settings.Required;
import de.tobias.utils.settings.SettingsSerializable;
import org.bukkit.configuration.MemorySection;

public class ApplicationInfo implements SettingsSerializable {

	private static final long serialVersionUID = 1L;

	@Key
	@Required
	private String name;
	@Key
	@Required
	private String identifier;
	@Key
	private String version;
	@Key
	@Required
	private long build;
	@Key
	private String author;
	@Key
	private String date;

	@Key
	private String basePath;
	@Key
	private String serverBaseURL;

	@Key
	private String updateURL;

	@Key
	private String main;
	@Key
	private String subMain;

	@Key
	private MemorySection userInfo;

	public String getName() {
		return name;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getVersion() {
		return version;
	}

	public long getBuild() {
		return build;
	}

	public String getAuthor() {
		return author;
	}

	public String getDate() {
		return date;
	}

	public String getUpdateURL() {
		return updateURL;
	}

	public String getMain() {
		return main;
	}

	public String getSubMain() {
		return subMain;
	}

	public String getBasePath() {
		return basePath;
	}

	public String getServerBaseURL() {
		return serverBaseURL;
	}

	public MemorySection getUserInfo() {
		return userInfo;
	}

	void setName(String name) {
		this.name = name;
	}

	void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	void setBuild(long build) {
		this.build = build;
	}
}
