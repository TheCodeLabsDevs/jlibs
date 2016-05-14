package de.tobias.utils.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class IOUtils {

	public static InputStream byteArrayToInputStream(byte[] b) {
		return new ByteArrayInputStream(b);
	}

	public static byte[] inputStreamToByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		copy(in, out);
		return out.toByteArray();
	}

	public static void copy(InputStream iStr, Path des) throws IOException {
		OutputStream oStr = Files.newOutputStream(des, StandardOpenOption.CREATE);
		copy(iStr, oStr);
		oStr.close();
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
