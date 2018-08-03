package de.tobias.utils.util.zip;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 * Eine Hilfsklasse zum Packen und Entpacken eines ZIP Archivs.
 *
 * @version 1.0
 */
public class ZIP {

	/**
	 * Pfad Separator des Betriebsystems.
	 * 
	 * <pre>
	 *      Windows: '\'
	 *      OS X (Unix): '/'
	 *      Linux: '/'
	 * </pre>
	 */
	private static final String separator;

	static {
		separator = System.getProperty("file.separator");
	}

	private ZIP() { // Um keine Instanz erzeugen zu können
	}

	/**
	 * Erstellt eine ZIP-Datei mit Inhalt.
	 *
	 * @param data
	 *            Quelldatei oder -ordner.
	 * @param zip
	 *            ZIP-Archive
	 * @throws IOException
	 *             Datein sind fehlerhaft
	 */
	public static void createZipFile(File data, File zip) throws IOException {
		ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zip));
		pack(data, data, outputStream);
		outputStream.close();
	}

	/**
	 * Erstellt eine ZIP-Datei mit Inhalt meherer Daten (Liste).
	 *
	 * @param data
	 *            Quelldateien oder -ordner.
	 * @param zip
	 *            ZIP-Archive
	 * @throws IOException
	 *             Dateien sind fehlerhaft
	 */
	public static void createZipFile(List<File> data, File zip) throws IOException {
		ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zip));
		for (File file : data) {
			pack(file, file, outputStream);
		}
		outputStream.close();
	}

	/**
	 * Packt die Daten. Die Verzeichnisse sind folgender Maßen gegliedert:
	 *
	 * <pre>
	 * 	OS X / Linux Beispiel (Gilt auch für Windows)
	 * 
	 *      Aktueller Pfad: 					/Users/Max/ChatingtonServer/users/Hans/conversation/mustermann.messages
	 *      Hauptpfad der Archivierten Daten: 	/Users/Max/ChatingtonServer/users
	 * 
	 *      Ergebis für die ZIP Datei: 			Hans/conversation/mustermann.messages
	 * </pre>
	 *
	 * @param file
	 *            Zu packende Datei
	 * @param root
	 *            Hauptverzeichnis
	 * @param outputStream
	 *            ZIP-Datei
	 * @throws IOException
	 *             Dateien sind fehlerhaft
	 */
	private static void pack(File file, File root, ZipOutputStream outputStream) throws IOException {
		if (file.exists()) {
			if (file.isFile()) {
				String newFileName = file.getAbsolutePath().replace(root.getAbsolutePath() + separator, "");

				FileInputStream inputStream = new FileInputStream(file);
				outputStream.putNextEntry(new ZipEntry(newFileName));

				copy(inputStream, outputStream);

				inputStream.close();
				outputStream.closeEntry();

			} else {
				if (file.listFiles() != null) {
					for (File data : file.listFiles()) {
						pack(data, root, outputStream);
					}
				}
			}
		}
	}

	/**
	 * Entpacken eines Archives.
	 *
	 * @param zip
	 *            ZIP Datei (Quelle)
	 * @param output
	 *            Hauptpfad zum entpacken.
	 * @param delOnExit
	 *            ZIP Löschen nach entpacken.
	 * @throws IOException
	 *             Dateien sind fehlerhaft
	 */
	public static void unpackZipFile(File zip, final File output, boolean delOnExit) throws IOException {
		final ZipFile zipfile = new ZipFile(zip);

		if (zipfile.size() != 0) {
			Collections.list(zipfile.entries()).forEach(new Consumer<ZipEntry>() {

				@Override
				public void accept(ZipEntry entry) {
					try {
						File file;
						if (separator.equals("\\"))
							file = new File(output.getAbsolutePath(), entry.getName().replace("/", "\\"));
						else
							file = new File(output.getAbsolutePath(), entry.getName().replace("\\", "/"));

						if (!entry.isDirectory()) {
							file.delete();

							file.getParentFile().mkdirs();
							file.createNewFile();

							FileOutputStream outputStream = new FileOutputStream(file);
							InputStream inputStream = zipfile.getInputStream(entry);

							copy(inputStream, outputStream);

							outputStream.close();
							inputStream.close();
						} else {
							file.mkdir();
						}
					} catch (NullPointerException | IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		zipfile.close();

		if (delOnExit) {
			zip.delete();
		}
	}

	private static void copy(InputStream iStr, OutputStream oStr) throws IOException {
		int count;
		byte[] buffer = new byte[4096];
		while ((count = iStr.read(buffer)) > 0)
			oStr.write(buffer, 0, count);
	}
}
