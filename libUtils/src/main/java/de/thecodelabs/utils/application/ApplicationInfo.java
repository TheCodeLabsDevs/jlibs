package de.thecodelabs.utils.application;

import de.thecodelabs.storage.settings.annotation.Key;
import de.thecodelabs.storage.settings.annotation.Required;

import java.util.Map;

public class ApplicationInfo {

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
	private Map<String, String> userInfo;

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

	public Map<String, String> getUserInfo() {
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
