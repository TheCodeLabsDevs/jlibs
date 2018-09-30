package de.tobias.utils.io;

import java.io.*;
import java.net.URL;
import java.nio.file.*;

public class IOUtils {

	/**
	 * Liest die angegebene URL als Bytearray ein
	 *
	 * @param url URL - URL
	 * @return byte[] - Bytearray
	 * @throws IOException
	 */
	public static byte[] urlToByteArray(URL url) throws IOException {
		InputStream in = url.openStream();
		return IOUtils.inputStreamToByteArray(in);
	}


	/**
	 * Liest die angegebene URL als String ein
	 *
	 * @param file URL - URL
	 * @return String - Inhalt der Datei
	 * @throws IOException
	 */
	public static String readURL(URL file) throws IOException {
		return new String(IOUtils.urlToByteArray(file));
	}


	public static InputStream byteArrayToInputStream(byte[] b) {
		return new ByteArrayInputStream(b);
	}

	public static byte[] inputStreamToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}

	public static void copy(InputStream iStr, Path des) throws IOException {
		Files.copy(iStr, des, StandardCopyOption.REPLACE_EXISTING);
	}

	public static void copy(InputStream iStr, OutputStream oStr) throws IOException {
		int count;
		byte[] buffer = new byte[1024];
		while ((count = iStr.read(buffer)) > 0)
			oStr.write(buffer, 0, count);
	}

	public static long getFreeSpaceForVolume(Path file) throws IOException {
		FileStore store = Files.getFileStore(file);
		return store.getUsableSpace();
	}

	public static long getTotalSpaceForVolume(Path file) throws IOException {
		FileStore store = Files.getFileStore(file);
		return store.getTotalSpace();
	}

	public static long getFreeSpaceForVolume(String file) throws IOException {
		return getFreeSpaceForVolume(Paths.get(file));
	}

	public static long getTotalSpaceForVolume(String file) throws IOException {
		return getTotalSpaceForVolume(Paths.get(file));
	}
}
