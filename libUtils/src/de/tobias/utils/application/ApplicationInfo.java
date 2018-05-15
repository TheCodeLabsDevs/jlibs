package de.tobias.utils.application;

import de.tobias.utils.settings.Required;
import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.Storable;
import org.bukkit.configuration.MemorySection;

public class ApplicationInfo implements SettingsSerializable {

	private static final long serialVersionUID = 1L;

	@Storable @Required private String name;
	@Storable @Required private String identifier;
	@Storable private String version;
	@Storable @Required private long build;
	@Storable private String author;
	@Storable
	private String date;

	@Storable private String basePath;

	@Storable private String updateURL;

	@Storable private String main;
	@Storable private String subMain;

	@Storable private MemorySection userInfo;

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
