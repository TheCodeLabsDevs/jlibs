package de.tobias.utils.application.container;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Consumer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.tobias.utils.application.App;
import de.tobias.utils.application.ApplicationInfo;
import de.tobias.utils.settings.YAMLSettings;
import de.tobias.utils.util.FileUtils;
import de.tobias.utils.util.SystemUtils;

public class FileContainer {

	private String containerName = "Java File Container";

	private FileContainerInfo containerInfo;
	private Path containerPath;
	private Path containerInfoPath;

	private ApplicationInfo info;
	private App app;

	public FileContainer(App app) {
		try {
			this.app = app;
			this.info = app.getInfo();
			this.containerName = app.getInfo().getBasePath();

			updatePath();

			if (Files.exists(containerInfoPath)) {
				containerInfo = YAMLSettings.load(FileContainerInfo.class, containerInfoPath);
			} else {
				containerInfo = new FileContainerInfo();
				saveInformation();
			}
		} catch (Exception e1) {
			System.err.println("Can't read container infos");
		}

		if (Files.notExists(containerPath)) {
			try {
				Files.createDirectories(containerPath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public FileContainer(String bundleIdentifier) throws Exception {
		containerPath = SystemUtils.getApplicationSupportDirectoryPath(containerName, bundleIdentifier);
		containerInfoPath = getPath("container.yml", PathType.ROOT);
		containerInfo = YAMLSettings.load(FileContainerInfo.class, containerInfoPath);
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
		Path path = containerPath.resolve(pathType.getFolder()).resolve(name);
		return path;
	}

	public Path getFolder(PathType type) {
		Path path = containerPath.resolve(type.getFolder());
		return path;
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

	@Deprecated
	public long availableBuild() {
		long version = -1;
		if (containerInfo.getUpdatePath() != null) {
			try {
				if (!containerInfo.getUpdatePath().endsWith("/")) {
					containerInfo.setUpdatePath(containerInfo.getUpdatePath() + "/");
				}
				URL url = new URL(containerInfo.getUpdatePath() + "/version.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(url.openStream());
				version = cfg.getLong("build");
			} catch (IOException ignored) {
			}
		}
		return version;
	}

	@Deprecated
	public String availableVersion() {
		String version = "";
		if (containerInfo.getUpdatePath() != null) {
			try {
				if (!containerInfo.getUpdatePath().endsWith("/")) {
					containerInfo.setUpdatePath(containerInfo.getUpdatePath() + "/");
				}
				URL url = new URL(containerInfo.getUpdatePath() + "/version.yml");
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(url.openStream());
				version = cfg.getString("version");
			} catch (IOException ignored) {
			}
		}
		return version;
	}

	@Deprecated
	public void updateApp(Consumer<Double> consumer) throws IOException {
		if (!containerInfo.getUpdatePath().endsWith("/")) {
			containerInfo.setUpdatePath(containerInfo.getUpdatePath() + "/");
		}
		URL url = new URL(containerInfo.getUpdatePath() + "version.yml");
		FileConfiguration cfg = YamlConfiguration.loadConfiguration(url.openStream());
		String path;
		if (containerInfo.getExecutionPath().toLowerCase().endsWith("jar")) {
			path = cfg.getString("pathJar");
		} else {
			path = cfg.getString("pathExe");
		}
		URL downloadUrl = new URL(path);

		HttpURLConnection httpConnection = (HttpURLConnection) (downloadUrl.openConnection());
		long completeFileSize = httpConnection.getContentLength();

		BufferedInputStream in = new BufferedInputStream(httpConnection.getInputStream());
		BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(containerInfo.getExecutionPath()), 1024);
		byte[] data = new byte[1024];
		long downloadedFileSize = 0;
		int x = 0;
		while ((x = in.read(data, 0, 1024)) >= 0) {
			downloadedFileSize += x;

			final double currentProgress = (double) downloadedFileSize / (double) completeFileSize;
			consumer.accept(currentProgress);
			bout.write(data, 0, x);
		}
		bout.close();
		in.close();
	}

	public void saveInformation() throws URISyntaxException {
		// Update der Informationen
		containerInfo.setExecutionPath(SystemUtils.getRunPath().toFile().getAbsolutePath());
		if (info.getUpdateURL() != null)
			containerInfo.setUpdatePath(info.getUpdateURL());
		containerInfo.setBuild(info.getBuild());
		containerInfo.setIdentifier(info.getIdentifier());
		containerInfo.setName(info.getName());

		try {
			YAMLSettings.save(containerInfo, containerInfoPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
