package de.tobias.dropbox;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;

public class DbxFileSystemProvider extends FileSystemProvider {

	private HashMap<String, DbxFileSystem> fileSystems;
	private DbxFileSystem defaultFileSystem;

	public DbxFileSystemProvider() {
		fileSystems = new HashMap<>();
	}

	@Override
	public String getScheme() {
		return "dropbox";
	}

	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env) throws IOException {
		DbxFileSystem fileSystem = new DbxFileSystem((DbxClientV2) env.get("client"), uri, this);
		if (fileSystems.isEmpty()) {
			defaultFileSystem = fileSystem;
		}

		fileSystems.put(uri.getHost(), fileSystem);

		return fileSystems.get(uri.getHost());
	}

	@Override
	public FileSystem getFileSystem(URI uri) {
		return fileSystems.get(uri.getHost());
	}

	@Override
	public Path getPath(URI uri) {
		if (uri.getHost() != null)
			return fileSystems.get(uri.getHost()).getPath(uri.getPath());
		else
			return defaultFileSystem.getPath(uri.getPath());
	}

	@Override
	public OutputStream newOutputStream(final Path path, OpenOption... options) throws IOException {
		final DbxClientV2 client = ((DbxPath) path).getFileSystem().getDbxClient();

		for (OpenOption o : options) {
			if (o == StandardOpenOption.APPEND) {
				throw new UnsupportedOperationException();
			}
		}

//		try {
//			final Uploader uploader = client.startUploadFile(path.toString(), DbxWriteMode.force(), -1);
//			return new OutputStream() {
//
//				@Override
//				public void write(int b) throws IOException {
//					uploader.getBody().write(b);
//				}
//
//				@Override
//				public void write(byte[] b, int off, int len) throws IOException {
//					uploader.getBody().write(b, off, len);
//				}
//
//				@Override
//				public void close() throws IOException {
//					try {
//						uploader.finish();
//						uploader.close();
//						for (OpenOption o : options) {
//							if (o == StandardOpenOption.DELETE_ON_CLOSE) {
//								client.files().delete(path.toString());
//							}
//						}
//					} catch (DbxException | RuntimeException e) {
//						e.printStackTrace();
//					}
//				}
//			};
//		} catch (DbxException e) {
//			e.printStackTrace();
//		}
		// TODO Implement
		return null;
	}

	@Override
	public InputStream newInputStream(Path path, OpenOption... options) throws IOException {
		final DbxClientV2 client = ((DbxPath) path).getFileSystem().getDbxClient();
//		try {
//			Downloader downloader = client.startGetFile(path.toString(), null);
//			return new InputStream() {
//
//				@Override
//				public int read() throws IOException {
//					return downloader.body.read();
//				}
//
//				@Override
//				public int read(byte[] b, int off, int len) throws IOException {
//					return downloader.body.read(b, off, len);
//				}
//
//				@Override
//				public void close() throws IOException {
//					downloader.close();
//					for (OpenOption o : options) {
//						if (o == StandardOpenOption.DELETE_ON_CLOSE) {
//							try {
//								client.delete(path.toString());
//							} catch (DbxException e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				}
//			};
//		} catch (DbxException e) {
//			e.printStackTrace();
//		}
		// TODO Implement
		return null;
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs) throws IOException {
		if (options.contains(StandardOpenOption.CREATE) || options.contains(StandardOpenOption.CREATE_NEW)
				|| options.contains(StandardOpenOption.WRITE)) {
			final WritableByteChannel wbc = Channels.newChannel(newOutputStream(path, options.toArray(new OpenOption[0])));
			long leftover = 0;

			final long offset = leftover;
			return new SeekableByteChannel() {

				long written = offset;

				public boolean isOpen() {
					return wbc.isOpen();
				}

				public long position() throws IOException {
					return written;
				}

				public SeekableByteChannel position(long pos) throws IOException {
					throw new UnsupportedOperationException();
				}

				public int read(ByteBuffer dst) throws IOException {
					throw new UnsupportedOperationException();
				}

				public SeekableByteChannel truncate(long size) throws IOException {
					throw new UnsupportedOperationException();
				}

				public int write(ByteBuffer src) throws IOException {
					int n = wbc.write(src);
					written += n;
					return n;
				}

				public long size() throws IOException {
					return written;
				}

				public void close() throws IOException {
					wbc.close();
				}
			};
		} else if (options.contains(StandardOpenOption.READ)) {
			ReadableByteChannel localReadableByteChannel = Channels.newChannel(newInputStream(path, options.toArray(new OpenOption[0])));
			return new SeekableByteChannel() {

				long read = 0L;

				public boolean isOpen() {
					return localReadableByteChannel.isOpen();
				}

				public long position() throws IOException {
					return this.read;
				}

				public SeekableByteChannel position(long paramAnonymousLong) throws IOException {
					throw new UnsupportedOperationException();
				}

				public int read(ByteBuffer paramAnonymousByteBuffer) throws IOException {
					int i = localReadableByteChannel.read(paramAnonymousByteBuffer);
					if (i > 0) {
						this.read += i;
					}
					return i;
				}

				public SeekableByteChannel truncate(long paramAnonymousLong) throws IOException {
					throw new NonWritableChannelException();
				}

				public int write(ByteBuffer paramAnonymousByteBuffer) throws IOException {
					throw new NonWritableChannelException();
				}

				public long size() throws IOException {
					return read;
				}

				public void close() throws IOException {
					localReadableByteChannel.close();
				};
			};
		}
		return null;
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter) throws IOException {
		DbxClientV2 client = ((DbxPath) dir).getFileSystem().getDbxClient();

//		DirectoryStream<Path> p = new DirectoryStream<Path>() {
//
//			@Override
//			public void close() throws IOException {
//
//			}
//
//			@Override
//			public Iterator<Path> iterator() {
//				List<DbxEntry> entries = null;
//				Iterator<DbxEntry> iterator;
//				try {
//					WithChildren childern = client.files().getMetadataWithChildren(dir.toString());
//					entries = childern.children;
//				} catch (DbxException e) {
//					entries = new ArrayList<>();
//					e.printStackTrace();
//				} finally {
//					iterator = entries.iterator();
//				}
//				return new Iterator<Path>() {
//
//					@Override
//					public boolean hasNext() {
//						return iterator.hasNext();
//					}
//
//					@Override
//					public Path next() {
//						return new DbxPath(iterator.next().path, ((DbxPath) dir).getFileSystem());
//					}
//				};
//			}
//		};
//		return p;
		// TODO Implement
		return null;
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs) throws IOException {
		if (dir instanceof DbxPath) {
			DbxFileSystem fileSystem = (DbxFileSystem) dir.getFileSystem();
			try {
				fileSystem.getDbxClient().files().createFolder(dir.toString());
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void delete(Path path) throws IOException {
		if (path instanceof DbxPath) {
			DbxFileSystem fileSystem = (DbxFileSystem) path.getFileSystem();
			try {
				fileSystem.getDbxClient().files().delete(path.toString());
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException {
		if (source instanceof DbxPath && target instanceof DbxPath) {
			DbxFileSystem sourceFileSystem = (DbxFileSystem) source.getFileSystem();
			DbxFileSystem targetFileSystem = (DbxFileSystem) target.getFileSystem();
			try {
				if (sourceFileSystem == targetFileSystem) {
					sourceFileSystem.getDbxClient().files().copy(source.toString(), target.toString());
				} else {
					InputStream iStr = sourceFileSystem.provider().newInputStream(source);
					OutputStream oStr = targetFileSystem.provider().newOutputStream(target);
//					IOUtils.copy(iStr, oStr); TODO Implement copy
					iStr.close();
					oStr.close();
				}
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException {
		if (source instanceof DbxPath && target instanceof DbxPath) {
			DbxFileSystem sourceFileSystem = (DbxFileSystem) source.getFileSystem();
			DbxFileSystem targetFileSystem = (DbxFileSystem) target.getFileSystem();
			try {
				if (sourceFileSystem == targetFileSystem) {
					sourceFileSystem.getDbxClient().files().move(source.toString(), target.toString());
				} else {
					InputStream iStr = sourceFileSystem.provider().newInputStream(source, StandardOpenOption.DELETE_ON_CLOSE);
					OutputStream oStr = targetFileSystem.provider().newOutputStream(target);
//					IOUtils.copy(iStr, oStr); TODO Implement
					iStr.close();
					oStr.close();
				}
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isSameFile(Path path, Path path2) throws IOException {
		return path.toString().equals(path2.toString());
	}

	@Override
	public boolean isHidden(Path path) throws IOException {
		return false;
	}

	@Override
	public FileStore getFileStore(Path path) throws IOException {
		return path.getFileSystem().getFileStores().iterator().next();
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException {
		if (path instanceof DbxPath) {

			DbxPath dbxPath = (DbxPath) path;
			try {
				if (dbxPath.getFileSystem().getDbxClient().files().getMetadata(path.toString()) == null) {
					throw new FileNotFoundException();
				}
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options) {
		if (path instanceof DbxPath) {
			return (V) new DbxFileAttributesView();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException {
		if (path instanceof DbxPath) {
			DbxPath dbxPath = (DbxPath) path;
			try {
				DbxFileAttributes attr = new DbxFileAttributes(dbxPath);
				return (A) attr;
			} catch (DbxException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException {
		// TODO
		return null;
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException {
		// TODO
	}

}
