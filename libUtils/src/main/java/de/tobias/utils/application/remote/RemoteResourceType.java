package de.tobias.utils.application.remote;

public enum RemoteResourceType {

	LIBRARY("Library"),
	RESOURCE("Resources"),
	UPDATE("Update");

	private String path;

	RemoteResourceType(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}
}
