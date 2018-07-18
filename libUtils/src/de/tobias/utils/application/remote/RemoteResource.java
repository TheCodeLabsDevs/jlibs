package de.tobias.utils.application.remote;

import de.tobias.utils.application.ApplicationUtils;
import de.tobias.utils.application.container.PathType;
import de.tobias.utils.util.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;

public class RemoteResource {

	private URLConnection urlConnection;

	public RemoteResource(URLConnection urlConnection) {
		this.urlConnection = urlConnection;
	}

	public InputStream getInputStream() {
		try {
			return urlConnection.getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void copy(PathType pathType, String childPath) {
		Path path = ApplicationUtils.getApplication().getPath(pathType, childPath);
		try {
			IOUtils.copy(getInputStream(), path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void safeCopy(PathType pathType, String childPath) {
		Path downloadPath = ApplicationUtils.getApplication().getPath(PathType.DOWNLOAD, childPath);
		Path path = ApplicationUtils.getApplication().getPath(pathType, childPath);
		try {
			IOUtils.copy(getInputStream(), downloadPath);
			Files.move(downloadPath, path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
