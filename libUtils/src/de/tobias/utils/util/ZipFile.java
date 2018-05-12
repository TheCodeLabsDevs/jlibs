package de.tobias.utils.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Collections;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;

public class ZipFile {

	public enum FileMode {
		READ,
		WRITE;
	}

	private ZipOutputStream outputStream;
	private java.util.zip.ZipFile zipFile;

	private Path path;
	private FileMode mode;

	public ZipFile(Path path, FileMode mode) throws IOException {
		this.mode = mode;

		if (mode == FileMode.READ) {
			zipFile = new java.util.zip.ZipFile(path.toFile());
		} else if (mode == FileMode.WRITE) {
			outputStream = new ZipOutputStream(new FileOutputStream(path.toFile()));
		}
	}

	public void addFile(Path externalSrc, Path internalDes) throws IOException {
		if (mode == FileMode.WRITE) {
			String newFileName = internalDes.toString();

			try {
				outputStream.putNextEntry(new ZipEntry(newFileName));
				FileInputStream inputStream = new FileInputStream(externalSrc.toFile());

				IOUtils.copy(inputStream, outputStream);

				inputStream.close();
				outputStream.closeEntry();
			} catch (ZipException e) {
				System.err.println(e.getLocalizedMessage());
			}
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
	}

	public void addFile(Path internalDes, Consumer<ZipOutputStream> consumer) throws IOException {
		if (mode == FileMode.WRITE) {
			String newFileName = internalDes.toString();

			try {
				outputStream.putNextEntry(new ZipEntry(newFileName));
				consumer.accept(outputStream);
				outputStream.closeEntry();
			} catch (ZipException e) {
				System.err.println(e.getLocalizedMessage());
			}
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
	}

	public InputStream inputStream(Path path) throws IOException {
		if (mode == FileMode.READ) {
			for (ZipEntry e : Collections.list(zipFile.entries())) {
				if (e.getName().equals(path.toFile().toString())) {
					return zipFile.getInputStream(e);
				}
			}
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
		return null;
	}

	public void getFile(Path internalSrc, Path externalDes) throws IOException {
		if (mode == FileMode.READ) {
			for (ZipEntry e : Collections.list(zipFile.entries())) {
				if (e.getName().equals(internalSrc.toFile().toString())) {
					getFile(e, externalDes);
					break;
				}
			}
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
	}

	public void getFile(ZipEntry entry, Path externalDes) throws IOException {
		if (mode == FileMode.READ) {
			if (!entry.isDirectory()) {
				FileOutputStream outputStream = new FileOutputStream(externalDes.toFile());
				InputStream inputStream = zipFile.getInputStream(entry);

				IOUtils.copy(inputStream, outputStream);

				outputStream.close();
				inputStream.close();
			}
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
	}

	public void list(Consumer<ZipEntry> consumer) {
		if (mode == FileMode.READ) {
			if (zipFile.size() != 0) {
				stream().forEach(consumer);
			}
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
	}

	public Stream<? extends ZipEntry> stream() {
		if (mode == FileMode.READ) {
			if (zipFile.size() != 0) {
				return Collections.list(zipFile.entries()).stream();
			}
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
		return null;
	}

	public void close() throws IOException {
		if (mode == FileMode.WRITE) {
			outputStream.close();
		} else if (mode == FileMode.READ) {
			zipFile.close();
		} else {
			throw new IllegalAccessError("Cannot access this in " + mode);
		}
	}

	public Path getPath() {
		return path;
	}

	public FileMode getMode() {
		return mode;
	}
}
