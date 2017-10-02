package de.tobias.utils.util;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import de.tobias.utils.application.NativeLoader;
import javafx.scene.image.Image;

public class SystemUtils {

	private static boolean loaded = false;

	public static String getPID() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name.substring(0, name.indexOf("@"));
	}

	@Deprecated
	public static File getApplicationSupportDirectory(String name) {
		switch (OS.getType()) {
		case Windows:
			return new File(System.getenv("APPDATA"), name);
		case MacOSX:
			return new File(System.getProperty("user.home"), "Library/Application Support/" + name);
		case Linux:
			return new File("/etc", name);
		default:
			return null;
		}
	}

	public static Path getApplicationSupportDirectoryPath(String name) {
		switch (OS.getType()) {
		case Windows:
			return Paths.get(System.getenv("APPDATA"), name);
		case MacOSX:
			return Paths.get(System.getProperty("user.home"), "Library/Application Support/", name);
		case Linux:
			return Paths.get("/opt", name);
		default:
			return null;
		}
	}

	public static Path getApplicationSupportDirectoryPath(String... name) {
		return getApplicationSupportDirectoryPath(StringUtils.build(name, File.separator));
	}

	public static Path getRunPath() throws URISyntaxException {
		return Paths.get(SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
	}

	public static boolean isJar() throws URISyntaxException {
		return getRunPath().toString().toLowerCase().endsWith(".jar");
	}

	public static boolean isExe() throws URISyntaxException {
		return getRunPath().toString().toLowerCase().endsWith(".exe");
	}

	// Linux only
	public static boolean isRootUser() {
		if (System.getenv().get("USER").equalsIgnoreCase("root")) {
			return true;
		} else {
			return false;
		}
	}

	@Deprecated
	public static Image getImageForFile(File file) {
		return getImageForFile(file.toPath());
	}

	public static Image getImageForFile(Path file) {
		load();
		return new Image(IOUtils.byteArrayToInputStream(getImageForFile_N(file.toString())));
	}

	private static void load() {
		if (!loaded) {
			NativeLoader.load("SystemUtils", "de/tobias/utils/util/assets/", SystemUtils.class);
			loaded = !loaded;
		}
	}

	private static native byte[] getImageForFile_N(String path);

	@Deprecated
	public static PrintStream convertStream(PrintStream stream, String prefix) {
		return new ConsoleStream(stream, prefix);
	}

	@Deprecated
	private static class ConsoleStream extends PrintStream {

		private SimpleDateFormat format = new SimpleDateFormat("dd-MM-YY HH:mm:ss");
		private String prefix;

		public ConsoleStream(OutputStream out, String prefix) {
			super(out);
			this.prefix = prefix;
		}

		@Override
		public void println(String obj) {
			super.println(prefix + " " + format.format(System.currentTimeMillis()) + ": " + obj);
		}
	}
}
