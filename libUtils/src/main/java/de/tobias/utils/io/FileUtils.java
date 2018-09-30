package de.tobias.utils.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.List;

public class FileUtils {

	// Byte Array

	/**
	 * Liest die angegebene Datei als Bytearray ein
	 *
	 * @param file Path - Pfad zur Datei
	 * @return byte[] - Bytearray
	 * @throws IOException
	 */
	public static byte[] fileToByteArray(Path file) throws IOException {
		return Files.readAllBytes(file);
	}

	/**
	 * Erzeugt aus einem Bytearray eine Datei
	 *
	 * @param b    byte[] - Bytearray
	 * @param file Path - Speicherort der Datei
	 * @throws IOException
	 */
	public static void byteArrayToFile(byte[] b, Path file) throws IOException {
		byteArrayToFile(b, file, StandardOpenOption.CREATE);
	}

	public static void byteArrayToFile(byte[] b, Path file, OpenOption... openOptions) throws IOException {
		Files.write(file, b, openOptions);
	}

	// Read

	/**
	 * Liest die angegebene Datei als String ein
	 *
	 * @param file Path - Pfad zur Datei
	 * @return String - Inhalt der Datei
	 * @throws IOException
	 */
	public static String readFile(Path file) throws IOException {
		return new String(fileToByteArray(file));
	}

	/**
	 * Liest die angegebene Datei zeilenweise ein
	 *
	 * @param file Path - Pfad zur Datei
	 * @return List<String> - Zeilen der Datei
	 * @throws IOException
	 */
	public static List<String> readLinesOfFile(Path file) throws IOException {
		return Files.readAllLines(file, Charset.defaultCharset());
	}

	/**
	 * Schreibt die angegebenen Zeilen in eine Datei
	 *
	 * @param file    Path - Speicherort der Datei
	 * @param content String - Inhalt, der gespeichert werden soll
	 * @throws IOException
	 */
	public static void writeFile(Path file, String content) throws IOException {
		Files.write(file, content.getBytes());
	}

	/**
	 * Schreibt die angegebenen Zeilen in eine Datei
	 *
	 * @param file  Path - Speicherort der Datei
	 * @param lines List<String> - Zeilen, die gespeichert werden sollen
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
	 * @param path  Path - Pfad zur Datei
	 * @param owner String - Besitzer
	 * @throws IOException
	 */
	public static void setOwner(Path path, String owner) throws IOException {
		UserPrincipalLookupService lookupService = path.getFileSystem().getUserPrincipalLookupService();
		UserPrincipal o = lookupService.lookupPrincipalByName(owner);
		Files.setOwner(path, o);
	}

	public static void deleteFilesInDirectory(Path dir) throws IOException {
		loopThroughDirectory(dir, new FileAction() {

			@Override
			public void onFile(Path file) throws IOException {
				try {
					Files.delete(file);
				} catch (SecurityException e) {
				}
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
	 * @param dir Path - Ordner
	 * @throws IOException
	 */
	public static void deleteDirectory(Path dir) throws IOException {
		if (Files.exists(dir)) {
			loopThroughDirectory(dir, new FileAction() {

				@Override
				public void onFile(Path file) throws IOException {
					try {
						Files.delete(file);
					} catch (SecurityException e) {
					}
				}

				@Override
				public void onDirectory(Path file) throws IOException {
					Files.delete(file);
				}
			});
		}
	}

	// Loop
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
		public void onFile(Path file) throws IOException {
		}

		@Override
		public void onDirectory(Path file) throws IOException {
		}
	}
}
