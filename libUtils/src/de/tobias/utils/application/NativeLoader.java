package de.tobias.utils.application;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import de.tobias.utils.application.container.PathType;
import de.tobias.utils.util.IOUtils;
import de.tobias.utils.util.OS;

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

	/**
	 * 
	 * @param basename
	 * @param folder
	 * @param clazz
	 * @param startup
	 * @param shutdown
	 */
	public static void load(String basename, String folder, Class<?> clazz, Thread startup, Thread shutdown) {
		try {
			Path libpath = copyLibrary(basename, folder, clazz);
			try {
				System.load(libpath.toString());
				if (startup != null)
					startup.start();

				if (shutdown != null)
					Runtime.getRuntime().addShutdownHook(shutdown);

				System.out.println("Loaded: " + libpath.toString());
			} catch (UnsatisfiedLinkError e) {
				System.err.println(e.getLocalizedMessage());
			}
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
			System.err.println("No version of " + basename + " available");
			return;
		}
	}

	/**
	 * 
	 * @param basename
	 * @param folder
	 * @param clazz
	 * @throws IOException
	 */
	protected static Path copyLibrary(String basename, String folder, Class<?> clazz) throws IOException {
		String name;
		if (folder.isEmpty()) {
			name = "";
		} else {
			name = folder + "/";
		}
		if (!basename.contains(".")) {
			switch (OS.getType()) {
			case Windows:
				name += basename + "Windows.dll";
				break;
			case MacOSX:
				name += "lib" + basename + "OSX.dylib";
				break;
			case Linux:
				name += "lib" + basename + "Linux.so";
				break;
			case Other:
				throw new UnsupportedOperationException("OS not supported: load native manually");
			}
		} else {
			folder += basename;
		}

		Path libpath = ApplicationUtils.getApplication().getPath(PathType.NATIVELIBRARY, name);

		Files.createDirectories(libpath.getParent());
		if (Files.notExists(libpath)) {
			Files.createFile(libpath);
		}
		try {
			InputStream iStr = clazz.getClassLoader().getResourceAsStream(name);
			IOUtils.copy(iStr, libpath);
			iStr.close();
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
		}
		return libpath;
	}
}
