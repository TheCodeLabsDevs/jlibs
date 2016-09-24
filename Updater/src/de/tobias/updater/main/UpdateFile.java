package de.tobias.updater.main;

import java.net.URL;
import java.nio.file.Path;

public class UpdateFile {

	private final URL url;
	private final Path local;

	public UpdateFile(URL url, Path local) {
		this.url = url;
		this.local = local;
	}

	public Path getLocal() {
		return local;
	}

	public URL getUrl() {
		return url;
	}
}
