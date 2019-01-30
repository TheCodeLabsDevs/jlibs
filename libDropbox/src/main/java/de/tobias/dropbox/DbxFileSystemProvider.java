package de.tobias.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadUploader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.file.*;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.spi.FileSystemProvider;
import java.util.*;

public class DbxFileSystemProvider extends FileSystemProvider
{

	private HashMap<String, DbxFileSystem> fileSystems;
	private DbxFileSystem defaultFileSystem;

	public DbxFileSystemProvider()
	{
		fileSystems = new HashMap<>();
	}

	@Override
	public String getScheme()
	{
		return "dropbox";
	}

	@Override
	public FileSystem newFileSystem(URI uri, Map<String, ?> env)
	{
		DbxFileSystem fileSystem = new DbxFileSystem((DbxClientV2) env.get("client"), uri, this);
		if(fileSystems.isEmpty())
		{
			defaultFileSystem = fileSystem;
		}

		fileSystems.put(uri.getHost(), fileSystem);

		return fileSystems.get(uri.getHost());
	}

	@Override
	public FileSystem getFileSystem(URI uri)
	{
		return fileSystems.get(uri.getHost());
	}

	@Override
	public Path getPath(URI uri)
	{
		if(uri.getHost() != null)
			return fileSystems.get(uri.getHost()).getPath(uri.getPath());
		else
			return defaultFileSystem.getPath(uri.getPath());
	}

	@Override
	public OutputStream newOutputStream(final Path path, OpenOption... options)
	{
		final DbxClientV2 client = ((DbxPath) path).getFileSystem().getDbxClient();

		for(OpenOption o : options)
		{
			if(o == StandardOpenOption.APPEND)
			{
				throw new UnsupportedOperationException();
			}
		}
		try
		{
			UploadUploader uploader = client.files().upload(path.toString());
			return new DbxOutputStream(uploader);
		}
		catch(DbxException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InputStream newInputStream(Path path, OpenOption... options)
	{
		final DbxClientV2 client = ((DbxPath) path).getFileSystem().getDbxClient();

		try
		{
			DbxDownloader<FileMetadata> downloader = client.files().download(path.toString());
			return downloader.getInputStream();
		}
		catch(DbxException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public SeekableByteChannel newByteChannel(Path path, Set<? extends OpenOption> options, FileAttribute<?>... attrs)
	{
		if(options.contains(StandardOpenOption.CREATE) || options.contains(StandardOpenOption.CREATE_NEW)
				|| options.contains(StandardOpenOption.WRITE))
		{
			final WritableByteChannel wbc = Channels.newChannel(newOutputStream(path, options.toArray(new OpenOption[0])));
			long leftover = 0;

			final long offset = leftover;
			return new SeekableByteChannel()
			{

				long written = offset;

				public boolean isOpen()
				{
					return wbc.isOpen();
				}

				public long position()
				{
					return written;
				}

				public SeekableByteChannel position(long pos)
				{
					throw new UnsupportedOperationException();
				}

				public int read(ByteBuffer dst)
				{
					throw new UnsupportedOperationException();
				}

				public SeekableByteChannel truncate(long size)
				{
					throw new UnsupportedOperationException();
				}

				public int write(ByteBuffer src) throws IOException
				{
					int n = wbc.write(src);
					written += n;
					return n;
				}

				public long size()
				{
					return written;
				}

				public void close() throws IOException
				{
					wbc.close();
				}
			};
		}
		else if(options.contains(StandardOpenOption.READ))
		{
			ReadableByteChannel localReadableByteChannel = Channels.newChannel(newInputStream(path, options.toArray(new OpenOption[0])));
			return new SeekableByteChannel()
			{

				long read = 0L;

				public boolean isOpen()
				{
					return localReadableByteChannel.isOpen();
				}

				public long position()
				{
					return this.read;
				}

				public SeekableByteChannel position(long paramAnonymousLong)
				{
					throw new UnsupportedOperationException();
				}

				public int read(ByteBuffer paramAnonymousByteBuffer) throws IOException
				{
					int i = localReadableByteChannel.read(paramAnonymousByteBuffer);
					if(i > 0)
					{
						this.read += i;
					}
					return i;
				}

				public SeekableByteChannel truncate(long paramAnonymousLong)
				{
					throw new NonWritableChannelException();
				}

				public int write(ByteBuffer paramAnonymousByteBuffer)
				{
					throw new NonWritableChannelException();
				}

				public long size()
				{
					return read;
				}

				public void close() throws IOException
				{
					localReadableByteChannel.close();
				}
			};
		}
		return null;
	}

	@Override
	public DirectoryStream<Path> newDirectoryStream(Path dir, Filter<? super Path> filter)
	{
		DbxClientV2 client = ((DbxPath) dir).getFileSystem().getDbxClient();

		return new DirectoryStream<Path>()
		{

			@Override
			public void close()
			{

			}

			@Override
			public Iterator<Path> iterator()
			{
				List<Metadata> entries = new ArrayList<>();
				Iterator<Metadata> iterator;
				try
				{
					ListFolderResult result;
					// Dopxbox möchte Root Dir als leeren Stirng
					if(dir.toString().equals("/"))
					{
						result = client.files().listFolder("");
					}
					else
					{
						result = client.files().listFolder(dir.toString());
					}
					while(true)
					{
						entries.addAll(result.getEntries());

						if(!result.getHasMore())
						{
							break;
						}

						result = client.files().listFolderContinue(result.getCursor());
					}
				}
				catch(DbxException e)
				{
					e.printStackTrace();
				}
				finally
				{
					iterator = entries.iterator();
				}
				return new Iterator<Path>()
				{

					@Override
					public boolean hasNext()
					{
						return iterator.hasNext();
					}

					@Override
					public Path next()
					{
						return new DbxPath(iterator.next().getPathDisplay(), ((DbxPath) dir).getFileSystem());
					}
				};
			}
		};
	}

	@Override
	public void createDirectory(Path dir, FileAttribute<?>... attrs)
	{
		if(dir instanceof DbxPath)
		{
			DbxFileSystem fileSystem = (DbxFileSystem) dir.getFileSystem();
			try
			{
				fileSystem.getDbxClient().files().createFolderV2(dir.toString());
			}
			catch(DbxException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void delete(Path path)
	{
		if(path instanceof DbxPath)
		{
			DbxFileSystem fileSystem = (DbxFileSystem) path.getFileSystem();
			try
			{
				fileSystem.getDbxClient().files().delete(path.toString());
			}
			catch(DbxException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public void copy(Path source, Path target, CopyOption... options) throws IOException
	{
		FileSystem sourceFileSystem = source.getFileSystem();
		FileSystem targetFileSystem = target.getFileSystem();
		try
		{
			if(sourceFileSystem == targetFileSystem && sourceFileSystem instanceof DbxFileSystem)
			{
				((DbxFileSystem) sourceFileSystem).getDbxClient().files().copy(source.toString(), target.toString());
			}
			else
			{
				InputStream iStr = sourceFileSystem.provider().newInputStream(source);
				OutputStream oStr = targetFileSystem.provider().newOutputStream(target);

				int count;
				byte[] buffer = new byte[1024];
				while((count = iStr.read(buffer)) > 0)
					oStr.write(buffer, 0, count);

				iStr.close();
				oStr.close();
			}
		}
		catch(DbxException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void move(Path source, Path target, CopyOption... options) throws IOException
	{
		DbxFileSystem sourceFileSystem = (DbxFileSystem) source.getFileSystem();
		DbxFileSystem targetFileSystem = (DbxFileSystem) target.getFileSystem();
		try
		{
			if(sourceFileSystem == targetFileSystem)
			{
				sourceFileSystem.getDbxClient().files().move(source.toString(), target.toString());
			}
			else
			{
				InputStream iStr = sourceFileSystem.provider().newInputStream(source, StandardOpenOption.DELETE_ON_CLOSE);
				OutputStream oStr = targetFileSystem.provider().newOutputStream(target);

				int count;
				byte[] buffer = new byte[1024];
				while((count = iStr.read(buffer)) > 0)
					oStr.write(buffer, 0, count);

				iStr.close();
				oStr.close();
			}
		}
		catch(DbxException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public boolean isSameFile(Path path, Path path2)
	{
		return path.toString().equals(path2.toString());
	}

	@Override
	public boolean isHidden(Path path)
	{
		return false;
	}

	@Override
	public FileStore getFileStore(Path path)
	{
		return path.getFileSystem().getFileStores().iterator().next();
	}

	@Override
	public void checkAccess(Path path, AccessMode... modes) throws IOException
	{
		if(path instanceof DbxPath)
		{

			DbxPath dbxPath = (DbxPath) path;
			try
			{
				if(dbxPath.getFileSystem().getDbxClient().files().getMetadata(path.toString()) == null)
				{
					throw new FileNotFoundException();
				}
			}
			catch(DbxException e)
			{
				throw new FileNotFoundException();
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <V extends FileAttributeView> V getFileAttributeView(Path path, Class<V> type, LinkOption... options)
	{
		if(path instanceof DbxPath)
		{
			return (V) new DbxFileAttributesView();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <A extends BasicFileAttributes> A readAttributes(Path path, Class<A> type, LinkOption... options) throws IOException
	{
		if(path instanceof DbxPath)
		{
			DbxPath dbxPath = (DbxPath) path;
			try
			{
				DbxFileAttributes attr = new DbxFileAttributes(dbxPath);
				return (A) attr;
			}
			catch(DbxException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public Map<String, Object> readAttributes(Path path, String attributes, LinkOption... options) throws IOException
	{
		// TODO
		return null;
	}

	@Override
	public void setAttribute(Path path, String attribute, Object value, LinkOption... options) throws IOException
	{
		// TODO
	}

}
