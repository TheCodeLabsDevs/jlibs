package de.tobias.updater;

import de.tobias.updater.main.UpdateFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class Updater {

	public interface UpdaterListener {
		void startDownload(double size);

		void updateProgress(double value, double readSize);
	}

	private UpdaterListener listener;

	public Updater(UpdaterListener listener) {
		this.listener = listener;
	}

	public synchronized Path downloadFile(Path downloadFolder, UpdateFile file) throws IOException {
		int readSize = 0;

		Path path = downloadFolder.resolve(file.getLocalPath().getFileName());
		if (Files.notExists(path)) {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}

		URLConnection conn = file.getUrl().openConnection();

		int size = Integer.valueOf(conn.getHeaderField("content-Length"));
		listener.startDownload(size);

		InputStream iStr = conn.getInputStream();
		OutputStream oStr = Files.newOutputStream(path);

		byte[] data = new byte[1024];
		int dataLength;
		while ((dataLength = iStr.read(data, 0, data.length)) > 0) {
			oStr.write(data, 0, dataLength);
			readSize += dataLength;

			listener.updateProgress(readSize / (double) size, readSize);
		}
		oStr.close();

		return path;
	}

	public synchronized void installFile(Path downloadFile, UpdateFile file) throws IOException {
		Files.copy(downloadFile, file.getLocalPath(), StandardCopyOption.REPLACE_EXISTING);
		Files.deleteIfExists(downloadFile);
	}
}
