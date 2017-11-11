package de.tobias.update.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.sun.nio.zipfs.ZipFileSystem;

import de.tobias.utils.application.App;
import de.tobias.utils.application.ApplicationInfo;
import de.tobias.utils.settings.YAMLSettings;
import de.tobias.utils.util.FileUtils;
import de.tobias.utils.util.OS;
import de.tobias.utils.util.OS.OSType;
import de.tobias.utils.util.WinZipInputStream;

public class AppMgrUtils {

	public static AppType checkDraggedFile(Path path) {
		if (FileUtils.getFileExtention(path).equalsIgnoreCase("jar")) {
			return AppType.JAR;
		} else if (OS.getType() == OSType.MacOSX) {
			if (FileUtils.getFileExtention(path).equalsIgnoreCase("app") && Files.isDirectory(path)) {
				return AppType.APP;
			}
		} else if (OS.getType() == OSType.Windows) {
			if (FileUtils.getFileExtention(path).equalsIgnoreCase("exe")) {
				return AppType.EXE;
			}
		}
		return AppType.NON;
	}

	public static App getApp(Path path, AppType type) {
		if (type == AppType.JAR) {
			Map<String, String> env = new HashMap<>();
			env.put("create", "true");
			try {
				FileSystem fs = FileSystems.newFileSystem(path, ZipFileSystem.class.getClassLoader());
				Path appYML = fs.getPath("application.yml");
				ApplicationInfo info = YAMLSettings.load(ApplicationInfo.class, Files.newInputStream(appYML));
				App app = new App(info);
				return app;
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (type == AppType.EXE) {
			try (ZipInputStream zis = new ZipInputStream(new WinZipInputStream(new FileInputStream(path.toString())))) {
				ZipEntry ze = null;
				while ((ze = zis.getNextEntry()) != null) {
					if (ze.getName().equals("application.yml")) {
						ApplicationInfo info = YAMLSettings.load(ApplicationInfo.class, zis);
						App app = new App(info);
						return app;
					}
					zis.closeEntry();
				}
				zis.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (type == AppType.APP) {
			try {
				for (Path jarPath : Files.newDirectoryStream(Paths.get(path.toString(), "Contents", "Java"))) {
					Map<String, String> env = new HashMap<>();
					env.put("create", "true");
					try {
						FileSystem fs = FileSystems.newFileSystem(jarPath, ZipFileSystem.class.getClassLoader());
						Path appYML = fs.getPath("/application.yml");
						ApplicationInfo info = YAMLSettings.load(ApplicationInfo.class, Files.newInputStream(appYML));
						App app = new App(info);
						return app;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		return null;
	}

	public enum AppType {
		JAR,
		APP,
		EXE,
		NON;
	}
}
