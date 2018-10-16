package de.tobias.utils.application.container;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.tobias.utils.application.App;
import de.tobias.utils.application.ApplicationInfo;
import de.tobias.utils.io.FileUtils;
import de.tobias.utils.io.PathUtils;
import de.tobias.utils.util.SystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileContainer {

	private String containerName = "Java File Container";

	private FileContainerInfo containerInfo;
	private Path containerPath;
	private Path containerInfoPath;

	private ApplicationInfo info;
	private App app;

	public FileContainer(App app) {
		this.app = app;
		this.info = app.getInfo();
		if (app.getInfo().getBasePath() != null && !app.getInfo().getBasePath().isEmpty()) {
			this.containerName = app.getInfo().getBasePath();
		}


		updatePath();

		if (Files.exists(containerInfoPath)) {
			containerInfo = Storage.load(containerInfoPath, StorageTypes.YAML, FileContainerInfo.class);
		} else {
			containerInfo = new FileContainerInfo();
			saveInformation();
		}

		PathUtils.createDirectoriesIfNotExists(containerPath);
	}

	public FileContainer(String bundleIdentifier) {
		containerPath = SystemUtils.getApplicationSupportDirectoryPath(containerName, bundleIdentifier);
		containerInfoPath = getPath("container.yml", PathType.ROOT);
		containerInfo = Storage.load(containerInfoPath, StorageTypes.YAML, FileContainerInfo.class);
	}

	public void updatePath() {
		if (app.isDebug()) {
			containerPath = SystemUtils.getApplicationSupportDirectoryPath(containerName, info.getIdentifier() + ".debug");
			containerInfoPath = getPath("container.yml", PathType.ROOT);
		} else {
			containerPath = SystemUtils.getApplicationSupportDirectoryPath(containerName, info.getIdentifier());
			containerInfoPath = getPath("container.yml", PathType.ROOT);
		}
	}

	public Path getPath(String name, PathType pathType) {
		final Path baseFolder = containerPath.resolve(pathType.getFolder());
		if (Files.notExists(baseFolder)) {
			try {
				Files.createDirectories(baseFolder);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		return baseFolder.resolve(name);
	}

	public Path getFolder(PathType type) {
		return containerPath.resolve(type.getFolder());
	}

	public Path getContainerPath() {
		return containerPath;
	}

	public Path getBackupPath(long time, Path path, PathType type) {
		return getFolder(PathType.BACKUP).resolve(Long.toString(time)).resolve(containerPath.relativize(path));
	}

	public FileContainerInfo getContainerInfo() {
		return containerInfo;
	}

	public void clear() throws IOException {
		FileUtils.deleteDirectory(getContainerPath());
	}


	public void saveInformation() {
		// Update der Informationen
		containerInfo.setExecutionPath(SystemUtils.getRunPath().toFile().getAbsolutePath());

		if (info.getUpdateURL() != null)
			containerInfo.setUpdatePath(info.getUpdateURL());

		containerInfo.setBuild(info.getBuild());
		containerInfo.setIdentifier(info.getIdentifier());
		containerInfo.setName(info.getName());

		Storage.save(containerInfoPath, StorageTypes.YAML, containerInfo);
	}
}
