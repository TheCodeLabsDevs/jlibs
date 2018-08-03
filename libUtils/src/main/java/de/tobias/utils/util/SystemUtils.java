package de.tobias.utils.util;

import de.tobias.utils.application.NativeLoader;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemUtils {

	private static boolean loaded = false;

	public static void loadNativeLibrary() {
		if (!loaded && OS.isMacOS()) {
			try {
				NativeLoader.copy("libUtilsNative.dylib", "de/tobias/utils/util/assets", SystemUtils.class);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			loaded = !loaded;
		}
	}

	public static String getPID() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name.substring(0, name.indexOf("@"));
	}

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

	// File Icon
	public static Image getImageForFile(Path file) {
		return new Image(IOUtils.byteArrayToInputStream(getImageForFile_N(file.toString())));
	}

	private static native byte[] getImageForFile_N(String path);
}
