package de.tobias.utils.application;

import de.tobias.utils.application.container.PathType;
import de.tobias.utils.util.FileUtils;
import de.tobias.utils.util.IOUtils;
import de.tobias.utils.util.OS;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class NativeLoader {

	/**
	 * Load native library from inside a jar file
	 *
	 * @param basename
	 * @param folder
	 * @param clazz
	 */
	public static void load(String basename, String folder, Class<?> clazz) {
		load(basename, folder, clazz, null, null);
	}

	/**
	 * Load native library from inside a jar file
	 *
	 * @param name
	 * @param clazz
	 */
	public static void load(String name, Class<?> clazz) {
		load(name, "", clazz, null, null);
	}

	public static void load(String basename, String folder, Class<?> clazz, Runnable startup, Runnable shutdown) {
		try {
			String filename = null;
			if (!basename.contains(".")) {
				switch (OS.getType()) {
					case Windows:
						filename = basename + "Windows.dll";
						break;
					case MacOSX:
						filename = "lib" + basename + "OSX.dylib";
						break;
					case Linux:
						filename = "lib" + basename + "Linux.so";
						break;
					case Other:
						throw new UnsupportedOperationException("OS not supported: load native manually");
				}
			} else {
				filename = basename;
			}
			Path libraryPath = copy(filename, folder, clazz);
			try {
				// Load file into jvm
				System.load(libraryPath.toString());

				if (startup != null)
					startup.run();

				if (shutdown != null)
					Runtime.getRuntime().addShutdownHook(new Thread(shutdown));

				System.out.println("Loaded: " + libraryPath.toString());
			} catch (UnsatisfiedLinkError e) {
				System.err.println(e.getLocalizedMessage());
			}
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
			System.err.println("No version of " + basename + " available");
		}
	}

	public static Path copy(String filename, String folder, Class<?> clazz) throws IOException {
		String name;
		if (folder.isEmpty()) {
			name = "";
		} else {
			name = folder + "/";
		}
		name += filename;

		Path libraryPath = ApplicationUtils.getApplication().getPath(PathType.NATIVE_LIBRARY, filename);

		Files.createDirectories(libraryPath.getParent());
		if (Files.notExists(libraryPath)) {
			Files.createFile(libraryPath);
		}
		try {
			InputStream iStr = clazz.getClassLoader().getResourceAsStream(name);
			IOUtils.copy(iStr, libraryPath);
			iStr.close();
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}
		return libraryPath;
	}

	private static final String[] nativeFileExtensions = {"dylib", "dll", "so"};

	public static boolean isNativeLibraryFile(Path path) {
		String extension = FileUtils.getFileExtension(path);
		for (String e : nativeFileExtensions) {
			if (e.equalsIgnoreCase(extension)) {
				return true;
			}
		}
		return false;
	}
}
