package de.thecodelabs.utils.application.resources.remote;

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
