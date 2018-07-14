package de.tobias.updater.client;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

public interface Updatable {

	int getCurrentBuild();

	String getCurrentVersion();

	int getNewBuild();

	String getNewVersion();

	boolean isUpdateAvailable();

	URL getDownloadPath();

	Path getLocalPath();

	String name();

	void loadInformation(UpdateChannel channel) throws IOException, URISyntaxException;

}
