package de.tobias.utils.util;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemUtils {

	public static Path getApplicationSupportDirectoryPath(String name) {
		switch (OS.getType()) {
			case Windows:
				return Paths.get(System.getenv("APPDATA"), name);
			case MacOSX:
				return Paths.get(System.getProperty("user.home"), "Library/Application Support/", name);
			case Linux:
				return Paths.get(System.getProperty("user.home"), "." + name);
			default:
				return null;
		}
	}

	public static Path getApplicationSupportDirectoryPath(String... name) {
		return getApplicationSupportDirectoryPath(StringUtils.build(name, File.separator));
	}

	public static Path getRunPath() {
		try {
			return Paths.get(SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean isJar() {
		return getRunPath().toString().toLowerCase().endsWith(".jar");
	}

	public static boolean isExe() {
		return getRunPath().toString().toLowerCase().endsWith(".exe");
	}

	// Linux only
	public static boolean isRootUser() {
		return System.getenv().get("USER").equalsIgnoreCase("root");
	}

}
