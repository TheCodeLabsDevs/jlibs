package de.tobias.updater.main;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UpdateFile {

	private final URL url;
	private final String local;

	public UpdateFile(URL url, String local) {
		this.url = url;
		this.local = local;
	}

	public String getLocal() {
		return local;
	}

	public Path getLocalPath() {
		return Paths.get(local);
	}

	public URL getUrl() {
		return url;
	}
}
