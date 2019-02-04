package de.thecodelabs.utils.application;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageType;
import de.thecodelabs.utils.application.container.ContainerPathType;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class AbstractResource
{
	public abstract InputStream getInputStream();

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

	public <T> T deserialize(StorageType storageType, Class<T> clazz) {
		return Storage.load(getInputStream(), storageType, clazz);
	}

	public void copy(ContainerPathType pathType, String childPath) {
		Path path = ApplicationUtils.getApplication().getPath(pathType, childPath);
		try {
			IOUtils.copy(getInputStream(), path);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public void safeCopy(ContainerPathType pathType, String childPath) {
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
