package de.tobias.dropbox;

import java.io.IOException;
import java.net.URI;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.dropbox.core.v2.DbxClientV2;

public class DbxFileSystem extends FileSystem {

	private DbxClientV2 client; // Dropbox connection
	private URI rootURI; // Host
	
	private DbxFileStore dbxFileStore;
	private DbxFileSystemProvider provider;

	public DbxFileSystem(DbxClientV2 client, URI uri, DbxFileSystemProvider provider) {
		this.client = client;
		this.rootURI = uri;
		this.provider = provider;
		
		dbxFileStore = new DbxFileStore(client);
	}

	public DbxClientV2 getDbxClient() {
		return client;
	}

	@Override
	public FileSystemProvider provider() {
		return provider;
	}

	@Override
	public void close() throws IOException {
	}

	@Override
	public boolean isOpen() {
		return true;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public String getSeparator() {
		return "/";
	}

	@Override
	public Iterable<Path> getRootDirectories() {
		return new ArrayList<Path>() {
			private static final long serialVersionUID = 1L;
			{
				add(new DbxPath(rootURI.getPath(), DbxFileSystem.this));
			}
		};
	}

	@Override
	public Iterable<FileStore> getFileStores() {
		Set<FileStore> stores = new HashSet<>();
		stores.add(dbxFileStore);
		return stores;
	}

	@Override
	public Set<String> supportedFileAttributeViews() {
		Set<String> views = new HashSet<>();
		views.add("dbx");
		return views;
	}

	@Override
	public Path getPath(String first, String... more) {
		String path = first;
		if (more.length != 0) {
			for (int i = 0; i < more.length; i++) {
				path += getSeparator() + more[i];
			}
		}
		return new DbxPath(path, this);
	}

	@Override
	public PathMatcher getPathMatcher(String syntaxAndPattern) {
		return new DbxPathMatcher(syntaxAndPattern);
	}

	@Override
	public UserPrincipalLookupService getUserPrincipalLookupService() {
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchService newWatchService() throws IOException {
		throw new UnsupportedOperationException();
	}

}
