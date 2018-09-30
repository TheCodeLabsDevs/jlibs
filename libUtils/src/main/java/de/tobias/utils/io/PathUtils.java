package de.tobias.utils.io;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathUtils {

	public static void createDirectoriesIfNotExists(Path path) {
		if (Files.notExists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	// Extensions

	/**
	 * Gibt die Dateiendungen für PNG und JPEG zurück (Groß- und Kleinschreibung)
	 *
	 * @return String[] - Dateiendungen
	 */
	public static String[] getImageExtension() {
		return new String[]{"*.png", "*.jpg", "*.jpeg", "*.PNG", "*.JPG", "*.JPEG"};
	}

	/**
	 * Prüft, ob die angegebene Datei eine Bilddatei ist
	 *
	 * @param file Path - Pfad zur Datei
	 * @return boolean - ist Bilddatei
	 */
	public static boolean hasImageExtension(Path file) {
		String extension = getFileExtension(file);
		for (String ex : getImageExtension()) {
			if (ex.contains(extension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prüft, ob die angegebene Datei eine Bilddatei ist
	 *
	 * @param file String - Datei
	 * @return boolean - ist Bilddatei
	 */
	public static boolean hasImageExtension(String file) {
		String extension = getFileExtension(file);
		for (String ex : getImageExtension()) {
			if (ex.contains(extension)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Prüft, ob die angegebene Datei eine Dateiendung hat
	 *
	 * @param file String - Datei
	 * @return boolean - Hat Dateiendung
	 */
	public static boolean hasExtension(String file) {
		return file.contains(".");
	}

	/**
	 * Gibt die Dateienedung für die angegebene Datei zurück
	 *
	 * @param file Path - Pfad zur Datei
	 * @return String - Dateiendung
	 */
	public static String getFileExtension(Path file) {
		return file.toString().substring(file.toString().lastIndexOf(".") + 1);
	}

	/**
	 * Gibt die Dateienedung für die angegebene Datei zurück
	 *
	 * @param file String - Datei
	 * @return String - Dateiendung
	 */
	public static String getFileExtension(String file) {
		return file.substring(file.lastIndexOf(".") + 1);
	}

	/**
	 * Setzt die Dateienedung für die angegebene Datei
	 *
	 * @param src       Path - Pfad zur Datei
	 * @param extension String - Dateiendung, die gesetzte werden soll
	 * @return Path - Datei mit gesetzter Dateiendung
	 */
	public static Path setExtension(Path src, String extension) {
		String name;
		if (hasExtension(src.toString())) {
			name = getFilenameWithoutExtension(src);
		} else {
			name = src.getFileName().toString();
		}

		if (extension.startsWith(".")) {
			name += extension;
		} else {
			name += "." + extension;
		}
		return Paths.get(src.getParent().toString(), name);
	}

	/**
	 * Gibt den Namen der angegebenen Datei ohne Dateiendung zurück
	 *
	 * @param file Path - Pfad zur Datei
	 * @return String - Dateiname
	 */
	public static String getFilenameWithoutExtension(Path file) {
		return file.toString().substring(0, file.toString().lastIndexOf("."));
	}

	public static boolean isSubDirectory(Path base, Path child) {
		Path parentFile = child;
		while (parentFile != null) {
			if (base.equals(parentFile)) {
				return true;
			}
			parentFile = parentFile.getParent();
		}
		return false;
	}
}
