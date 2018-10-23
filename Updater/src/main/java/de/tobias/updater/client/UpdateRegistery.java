package de.tobias.updater.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.thecodelabs.utils.util.OS;
import de.thecodelabs.utils.util.SystemUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

public class UpdateRegistery {

	private static Set<Updatable> updatables = new HashSet<>();
	private static Set<Updatable> availableUpdates = new HashSet<>();

	public static void registerUpdateable(Updatable updatable) {
		updatables.add(updatable);
	}

	public static Set<Updatable> getAvailableUpdates() {
		return availableUpdates;
	}

	public static Set<Updatable> lookupUpdates(UpdateChannel channel) {
		availableUpdates.clear();
		for (Updatable updatable : UpdateRegistery.updatables) {
			try {
				updatable.loadInformation(channel);
				if (updatable.isUpdateAvailable()) {
					availableUpdates.add(updatable);
				}
			} catch (IOException | URISyntaxException e) {

			}
		}
		return availableUpdates;
	}

	private static final String DOWNLOAD_PATH = "downloadPath";
	private static final String FILES = "files";
	private static final String LOCAL = "local";
	private static final String URL = "url";
	private static final String EXECUTE_FILE = "executePath";

	public static String buildParamaterString(String downloadPath) {
		JsonObject data = new JsonObject();
		data.addProperty(DOWNLOAD_PATH, downloadPath);

		JsonArray array = new JsonArray();
		for (Updatable updatable : availableUpdates) {
			JsonObject file = new JsonObject();
			file.addProperty(URL, updatable.getDownloadPath().toString());
			file.addProperty(LOCAL, updatable.getLocalPath().toString());
			array.add(file);
		}
		data.add(FILES, array);
		data.addProperty(EXECUTE_FILE, SystemUtils.getRunPath().toString());

		return data.toString();
	}

	public static boolean needsAdminPermission() {
		for (Updatable updatable : availableUpdates) {
			if (!Files.isWritable(updatable.getLocalPath())) {
				return true;
			}
			if (OS.isWindows()) {
				try {
					if (Files.getOwner(updatable.getLocalPath()).getName().toLowerCase().contains("admin")) {
						return true;
					}
				} catch (IOException ignored) {
				}
			}
		}
		return false;
	}
}
