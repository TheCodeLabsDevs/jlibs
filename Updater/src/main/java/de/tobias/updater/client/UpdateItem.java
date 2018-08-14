package de.tobias.updater.client;

import de.tobias.utils.settings.Key;

public class UpdateItem {

	@Key
	private int build;
	@Key
	private String version;
	@Key
	private String pathJar;
	@Key
	private String pathExe;
	@Key
	private String pathApp;

	public int getBuild() {
		return build;
	}

	public void setBuild(int build) {
		this.build = build;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getPathJar() {
		return pathJar;
	}

	public void setPathJar(String pathJar) {
		this.pathJar = pathJar;
	}

	public String getPathExe() {
		return pathExe;
	}

	public void setPathExe(String pathExe) {
		this.pathExe = pathExe;
	}

	public String getPathApp() {
		return pathApp;
	}

	public void setPathApp(String pathApp) {
		this.pathApp = pathApp;
	}
}
