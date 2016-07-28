package de.tobias.dropbox;

import java.io.IOException;
import java.io.OutputStream;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.files.UploadUploader;

public class DbxOutputStream extends OutputStream {

	private UploadUploader uploader;

	public DbxOutputStream(UploadUploader uploader) {
		this.uploader = uploader;
	}

	@Override
	public void write(int b) throws IOException {
		uploader.getOutputStream().write(b);
	}

	@Override
	public void close() throws IOException {
		try {
			uploader.finish();
		} catch (DbxException e) {
			e.printStackTrace();
		}
	}

}
