package de.tobias.dropbox;

import java.io.IOException;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import com.dropbox.core.DbxException;

public class DbxFileAttributes implements BasicFileAttributes {

	private DbxPath path;
	// private DbxEntry entry;
	// private DbxEntry.File file;

	public DbxFileAttributes(DbxPath path) throws DbxException, IOException {
		// this.path = path;
		// entry = path.getFileSystem().getDbxClient().getMetadata(path.toString());
		//
		// if (entry == null) {
		// throw new FileNotFoundException();
		// }
	}

	@Override
	public FileTime lastModifiedTime() {
		// if (file == null)
		// try {
		// file = path.getFileSystem().getDbxClient().getFile(path.toString(), null, new ByteArrayOutputStream());
		// } catch (DbxException | IOException e) {
		// e.printStackTrace();
		// }
		// return FileTime.fromMillis(file.lastModified.getTime());
		// TODO Implement
		return null;
	}

	@Override
	public FileTime lastAccessTime() {
		// if (file == null)
		// try {
		// file = path.getFileSystem().getDbxClient().getFile(path.toString(), null, new ByteArrayOutputStream());
		// } catch (DbxException | IOException e) {
		// e.printStackTrace();
		// }
		// return FileTime.fromMillis(file.lastModified.getTime());
		// TODO Implement
		return null;
	}

	@Override
	public FileTime creationTime() {
		// if (file == null)
		// try {
		// file = path.getFileSystem().getDbxClient().getFile(path.toString(), null, new ByteArrayOutputStream());
		// } catch (DbxException | IOException e) {
		// e.printStackTrace();
		// }
		// return FileTime.fromMillis(file.lastModified.getTime());
		// TODO Implement
		return null;
	}

	@Override
	public boolean isRegularFile() {
		// TODO Implement
		return false;
	}

	@Override
	public boolean isDirectory() {
		// TODO Implement
		return false;
	}

	@Override
	public boolean isSymbolicLink() {
		return false;
	}

	@Override
	public boolean isOther() {
		return false;
	}

	@Override
	public long size() {
		// if (file == null)
		// try {
		// file = path.getFileSystem().getDbxClient().getFile(path.toString(), null, new ByteArrayOutputStream());
		// } catch (DbxException | IOException e) {
		// e.printStackTrace();
		// }
		// return file.numBytes;
		// TODO Implement
		return 0l;
	}

	@Override
	public Object fileKey() {
		// TODO
		return null;
	}

}
