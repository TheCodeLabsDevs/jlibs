package de.tobias.update.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;

import com.sun.nio.zipfs.ZipFileSystem;

import de.tobias.update.main.AppMgrUtils.AppType;
import de.tobias.utils.application.App;
import de.tobias.utils.application.container.FileContainer;
import de.tobias.utils.util.SystemUtils;
import de.tobias.utils.util.WinZipInputStream;

public class Update {

	public enum Status {
		NOTSTARTED,
		BEGIN,
		FINISH;
	}

	private App app;
	private long newBuild;
	private String newVersion;

	private AppType type;
	private Path path;

	private BooleanProperty updateEnableProperty;
	private ObjectProperty<Status> status;

	public Update(Path executablePath, long build, String version) throws IOException {
		this.path = executablePath;
		if (executablePath.toString().endsWith(".jar")) {
			this.type = AppType.JAR;
			app = AppMgrUtils.getApp(executablePath, AppType.JAR);
		} else {
			this.type = AppType.EXE;
			app = AppMgrUtils.getApp(executablePath, AppType.EXE);
		}
		this.newVersion = version;
		this.newBuild = build;
		this.status = new SimpleObjectProperty<Update.Status>(Status.NOTSTARTED);

		this.updateEnableProperty = new SimpleBooleanProperty();
	}

	public boolean getUpdateEnable() {
		return updateEnableProperty.get();
	}

	public void setUpdateEnable(boolean updateEnableProperty) {
		this.updateEnableProperty.set(updateEnableProperty);
	}

	public BooleanProperty updateEnableProperty() {
		return updateEnableProperty;
	}

	public Status getStatus() {
		return status.get();
	}

	public void setStatus(Status status) {
		this.status.set(status);
	}

	public ObjectProperty<Status> statusProterty() {
		return status;
	}

	public App getApp() {
		return app;
	}

	@Override
	public String toString() {
		return app.getInfo().getName();
	}

	public long getNewBuild() {
		return newBuild;
	}

	public String getNewVersion() {
		return newVersion;
	}

	public InputStream loadFileFromArchive(String name) throws IOException {
		if (type == AppType.JAR) {
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			FileSystem fs = FileSystems.newFileSystem(path, ZipFileSystem.class.getClassLoader());
			Path appYML = fs.getPath(name);
			return Files.newInputStream(appYML);

		} else if (type == AppType.EXE) {
			ZipInputStream zis = new ZipInputStream(new WinZipInputStream(new FileInputStream(path.toString())));
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null) {
				if (ze.getName().equals(name)) {
					return zis;
				}
				zis.closeEntry();
			}
			zis.close();

		}
		return null;
	}

	public static Update load(String fileName) throws Exception {
		if (SystemUtils.isJar() || SystemUtils.isExe()) {
			FileContainer container = new FileContainer(fileName);
			if (container.getContainerInfo().getExecutionPath() != null) {
				long build = container.availableBuild();
				if (build != -1) {
					String version = container.availableVersion();
					Update update = new Update(Paths.get(container.getContainerInfo().getExecutionPath()), build, version);
					if (build > update.getApp().getInfo().getBuild()) {
						return update;
					}
				}
			}
		}
		return null;
	}
}
