package de.tobias.utils.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.List;

public class FileUtils {

	/**
	 * Prüft, ob die angegebene Datei Kindelement des angegebenen Ordners ist
	 * 
	 * @param children
	 *            String - Datei
	 * @param parent
	 *            String - Ordner
	 * @return boolean - Datei ist Kindelement vom Ordner
	 */
	public static boolean isFileChildernOfPath(String childern, String parent) {
		return childern.replace("\\", "/").matches("^" + parent.replace("\\", "/") + ".*$");
	}

	// Extensions
	/**
	 * Gibt die Dateiendungen für PNG und JPEG zurück (Groß- und Kleinschreibung)
	 * 
	 * @return String[] - Dateiendungen
	 */
	public static String[] getImageExtension() {
		return new String[] { "*.png", "*.jpg", "*.jpeg", "*.PNG", "*.JPG", "*.JPEG" };
	}

	/**
	 * Prüft, ob die angegebene Datei eine Bilddatei ist
	 * 
	 * @param file
	 *            Path - Pfad zur Datei
	 * @return boolean - ist Bilddatei
	 */
	public static boolean hasImageExtension(Path file) {
		String extension = getFileExtention(file);
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
	 * @param file
	 *            String - Datei
	 * @return boolean - ist Bilddatei
	 */
	public static boolean hasImageExtension(String file) {
		String extension = getFileExtention(file);
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
	 * @param file
	 *            String - Datei
	 * @return boolean - Hat Dateiendung
	 */
	public static boolean hasExtension(String file) {
		return file.contains(".");
	}

	/**
	 * Gibt die Dateienedung für die angegebene Datei zurück
	 * 
	 * @param file
	 *            Path - Pfad zur Datei
	 * @return String - Dateiendung
	 */
	public static String getFileExtention(Path file) {
		return file.toString().substring(file.toString().lastIndexOf(".") + 1);
	}

	/**
	 * Gibt die Dateienedung für die angegebene Datei zurück
	 * 
	 * @param file
	 *            String - Datei
	 * @return String - Dateiendung
	 */
	public static String getFileExtention(String file) {
		return file.substring(file.lastIndexOf(".") + 1);
	}

	/**
	 * Setzt die Dateienedung für die angegebene Datei
	 * 
	 * @param src
	 *            Path - Pfad zur Datei
	 * @param extension
	 *            String - Dateiendung, die gesetzte werden soll
	 * @return Path - Datei mit gesetzter Dateiendung
	 */
	public static Path setExtension(Path src, String extension) {
		String name;
		if (hasExtension(src.toString())) {
			name = getFilenameWithoutExtention(src);
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
	 * @param file
	 *            Path - Pfad zur Datei
	 * @return String - Dateiname
	 */
	public static String getFilenameWithoutExtention(Path file) {
		return file.toString().substring(0, file.toString().lastIndexOf("."));
	}

	// Byte Array
	/**
	 * Liest die angegebene Datei als Bytearray ein
	 * 
	 * @param file
	 *            Path - Pfad zur Datei
	 * @return byte[] - Bytearray
	 * @throws IOException
	 */
	public static byte[] fileToByteArray(Path file) throws IOException {
		return Files.readAllBytes(file);
	}

	/**
	 * Erzeugt aus einem Bytearray eine Datei
	 * 
	 * @param b
	 *            byte[] - Bytearray
	 * @param file
	 *            Path - Speicherort der Datei
	 * @throws IOException
	 */
	public static void byteArrayToFile(byte[] b, Path file) throws IOException {
		Files.write(file, b, StandardOpenOption.CREATE);
	}

	/**
	 * Liest die angegebene URL als Bytearray ein
	 * 
	 * @param file
	 *            URL - URL
	 * @return byte[] - Bytearray
	 * @throws IOException
	 */
	public static byte[] urlToByteArray(URL url) throws IOException {
		InputStream in = url.openStream();
		return IOUtils.inputStreamToByteArray(in);
	}

	// Read
	/**
	 * Liest die angegebene Datei als String ein
	 * 
	 * @param file
	 *            Path - Pfad zur Datei
	 * @return String - Inhalt der Datei
	 * @throws IOException
	 */
	public static String readFile(Path file) throws IOException {
		return new String(fileToByteArray(file));
	}

	/**
	 * Liest die angegebene URL als String ein
	 * 
	 * @param file
	 *            URL - URL
	 * @return String - Inhalt der Datei
	 * @throws IOException
	 */
	public static String readURL(URL file) throws IOException {
		return new String(urlToByteArray(file));
	}

	/**
	 * Liest die angegebene Datei zeilenweise ein
	 * 
	 * @param file
	 *            Path - Pfad zur Datei
	 * @return List<String> - Zeilen der Datei
	 * @throws IOException
	 */
	public static List<String> readLinesOfFile(Path file) throws IOException {
		return Files.readAllLines(file, Charset.defaultCharset());
	}

	/**
	 * Schreibt die angegebenen Zeilen in eine Datei
	 * 
	 * @param file
	 *            Path - Speicherort der Datei
	 * @param lines
	 *            List<String> - Zeilen, die gespeichert werden sollen
	 * @throws IOException
	 */
	public static void writeLines(Path file, List<String> lines) throws IOException {
		PrintWriter writer = new PrintWriter(Files.newOutputStream(file, StandardOpenOption.CREATE));
		for (String line : lines) {
			writer.println(line);
		}
		writer.close();
	}

	// Util
	/**
	 * Setzt den Besitzer für die angegebene Datei
	 * 
	 * @param path
	 *            Path - Pfad zur Datei
	 * @param owner
	 *            String - Besitzer
	 * @throws IOException
	 */
	public static void setOwner(Path path, String owner) throws IOException {
		UserPrincipalLookupService lookupService = path.getFileSystem().getUserPrincipalLookupService();
		UserPrincipal o = lookupService.lookupPrincipalByName(owner);
		Files.setOwner(path, o);
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

	public static void deleteFilesInDirectory(Path dir) throws IOException {
		loopThroughDirectory(dir, new FileAction() {

			@Override
			public void onFile(Path file) throws IOException {
				try {
					Files.delete(file);
				} catch (SecurityException e) {}
			}

			@Override
			public void onDirectory(Path file) throws IOException {
				if (!file.equals(dir)) {
					Files.delete(file);
				}
			}
		});
	}

	/**
	 * Löscht alle Dateien und Verzeichnisse innerhalb des angegebenen Ordners
	 * 
	 * @param dir
	 *            Path - Ordner
	 * @throws IOException
	 */
	public static void deleteDirectory(Path dir) throws IOException {
		if (Files.exists(dir)) {
			loopThroughDirectory(dir, new FileAction() {

				@Override
				public void onFile(Path file) throws IOException {
					try {
						Files.delete(file);
					} catch (SecurityException e) {}
				}

				@Override
				public void onDirectory(Path file) throws IOException {
					Files.delete(file);
				}
			});
		}
	}

	// Loop
	@Deprecated
	public static void loopThroughDirecoriesWithFilter(Path root, FileAction action, Filter<Path> filter) throws IOException {
		if (Files.isDirectory(root)) {
			DirectoryStream<Path> files = Files.newDirectoryStream(root, filter);
			if (files != null) {
				for (Path path : files) {
					loopThroughDirecoriesWithFilter(path, action, filter);
				}
			}
			files.close();
			action.onDirectory(root);
		} else {
			action.onFile(root);
		}
	}
	
	public static void loopThroughDirectoriesWithFilter(Path root, FileAction action, Filter<Path> filter) throws IOException {
		if (Files.isDirectory(root)) {
			DirectoryStream<Path> files = Files.newDirectoryStream(root, filter);
			if (files != null) {
				for (Path path : files) {
					loopThroughDirectoriesWithFilter(path, action, filter);
				}
			}
			files.close();
			action.onDirectory(root);
		} else {
			action.onFile(root);
		}
	}

	public static void loopThroughDirectory(Path root, FileAction action) throws IOException {
		loopThroughDirectoriesWithFilter(root, action, (entry) -> true);
	}

	public interface FileAction {

		public void onFile(Path file) throws IOException;

		public void onDirectory(Path file) throws IOException;
	}

	public static class FileActionAdapter implements FileAction {

		@Override
		public void onFile(Path file) throws IOException {}

		@Override
		public void onDirectory(Path file) throws IOException {}
	}
}
