package de.thecodelabs.utils.application.remote;

import com.google.gson.Gson;
import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.Charset;
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

	public byte[] getAsByteArray() {
		try {
			return IOUtils.inputStreamToByteArray(getInputStream());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getAsString() {
		return new String(getAsByteArray());
	}

	public String getAsString(Charset charset) {
		return new String(getAsByteArray(), charset);
	}

	public <T> T getAsJson(Class<T> clazz) {
		return new Gson().fromJson(new InputStreamReader(getInputStream()), clazz);
	}

	public <T> T getAsYaml(Class<T> clazz) {
		return Storage.load(getInputStream(), StorageTypes.YAML, clazz);
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
