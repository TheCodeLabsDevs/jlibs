package de.tobias.utils.application;

import org.bukkit.configuration.MemorySection;

import de.tobias.utils.settings.Required;
import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.Storable;

public class ApplicationInfo implements SettingsSerializable {

	private static final long serialVersionUID = 1L;

	@Storable @Required private String name;
	@Storable @Required private String identifier;
	@Storable private String version;
	@Storable @Required private long build;
	@Storable private String author;

	@Storable private String updateURL;

	@Storable private String main;
	@Storable private String subMain;

	@Storable private boolean backup;

	@Storable private String helpmap;
	@Storable private String iconPath;
	@Storable private String iconFullPath;

	@Storable private String splashScreenImage;

	@Storable private MemorySection userInfo;

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * @return the version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @return the build
	 */
	public long getBuild() {
		return build;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
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

	public boolean isBackup() {
		return backup;
	}

	/**
	 * @return the helpmap
	 */
	public String getHelpmap() {
		return helpmap;
	}

	public String getIconPath() {
		return iconPath;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	void setName(String name) {
		this.name = name;
	}

	/**
	 * @param identifier
	 *            the identifier to set
	 */
	void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	/**
	 * @param build
	 *            the build to set
	 */
	void setBuild(long build) {
		this.build = build;
	}

	void setBackup(boolean backup) {
		this.backup = backup;
	}

	public String getSlashScreenImage() {
		return splashScreenImage;
	}

	public MemorySection getUserInfo() {
		return userInfo;
	}
}
