package de.tobias.dropbox;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

public class DbxFileStore extends FileStore {

	private DbxClientV2 client;

	public DbxFileStore(DbxClientV2 client) {
		this.client = client;
	}

	@Override
	public String name() {
		try {
			return client.users().getCurrentAccount().getName().getDisplayName().replace(" ", "%20");
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String type() {
		return "dbx";
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public long getTotalSpace() throws IOException {
		try {
			return client.users().getSpaceUsage().getAllocation().getIndividualValue().getAllocated();
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long getUsableSpace() throws IOException {
		try {
			long allocated = client.users().getSpaceUsage().getAllocation().getIndividualValue().getAllocated();
			long used = client.users().getSpaceUsage().getUsed();
			return allocated - used;
		} catch (DbxException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public long getUnallocatedSpace() throws IOException {
		return 0;
	}

	@Override
	public boolean supportsFileAttributeView(Class<? extends FileAttributeView> type) {
		return false;
	}

	@Override
	public boolean supportsFileAttributeView(String name) {
		return false;
	}

	@Override
	public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> type) {
		return null;
	}

	@Override
	public Object getAttribute(String attribute) throws IOException {
		if (attribute.equals("totalSpace"))
			return getTotalSpace();
		if (attribute.equals("usableSpace"))
			return getUsableSpace();
		if (attribute.equals("unallocatedSpace"))
			return getUnallocatedSpace();
		throw new UnsupportedOperationException("'" + attribute + "' not recognized");
	}

}
