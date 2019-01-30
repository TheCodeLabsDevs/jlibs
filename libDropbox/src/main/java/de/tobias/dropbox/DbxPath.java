package de.tobias.dropbox;

import java.io.File;
import java.net.URI;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class DbxPath implements Path
{

	private String path;
	private DbxFileSystem fileSystem;

	DbxPath(String path, DbxFileSystem fileSystem)
	{
		this.path = path;
		this.fileSystem = fileSystem;
	}

	@Override
	public DbxFileSystem getFileSystem()
	{
		return fileSystem;
	}

	@Override
	public boolean isAbsolute()
	{
		return path.startsWith(fileSystem.getSeparator());
	}

	@Override
	public Path getRoot()
	{
		if(path.length() > 0 && path.charAt(0) == '/')
		{
			return getFileSystem().getRootDirectories().iterator().next();
		}
		else
		{
			return null;
		}
	}

	private String appendFileName(String name)
	{
		String path = this.path;
		if(path.endsWith(fileSystem.getSeparator()) || path.startsWith(fileSystem.getSeparator()))
		{
			path += name;
		}
		else
		{
			path += fileSystem.getSeparator() + name;
		}
		return path;
	}

	private String reducePathByElementCount(int count)
	{
		String path = "";
		for(int i = 0; i < getNameCount() - count; i++)
		{
			path = getName(i).toString();
			if(i + 1 < getNameCount() - count)
			{
				path += fileSystem.getSeparator();
			}
		}
		return path;
	}

	private String reduceAndAppend(Path path)
	{
		String s1 = reducePathByElementCount(path.getNameCount());
		DbxPath p2 = new DbxPath(s1, fileSystem);
		return p2.appendFileName(path.toString());
	}

	@Override
	public Path getFileName()
	{
		// +1, da sonst der letzte / noch mit verwendet wird
		return new DbxPath(path.substring(path.lastIndexOf(fileSystem.getSeparator()) + 1), fileSystem);
	}

	@Override
	public Path getParent()
	{
		return new DbxPath(path.substring(0, path.lastIndexOf(fileSystem.getSeparator())), fileSystem);
	}

	@Override
	public int getNameCount()
	{
		return path.split(fileSystem.getSeparator()).length;
	}

	@Override
	public Path getName(int index)
	{
		String[] paths = path.split(fileSystem.getSeparator());
		if(paths.length > index)
			return new DbxPath(paths[index], fileSystem);
		else
			return null;
	}

	@Override
	public Path subpath(int beginIndex, int endIndex)
	{
		StringBuilder name = new StringBuilder();
		for(int i = beginIndex; i <= endIndex; i++)
		{
			name.append(getName(i));
			if(i + 1 <= endIndex)
			{
				name.append(fileSystem.getSeparator());
			}
		}
		return new DbxPath(name.toString(), fileSystem);
	}

	@Override
	public boolean startsWith(Path other)
	{
		return path.startsWith(other.toString());
	}

	@Override
	public boolean startsWith(String other)
	{
		return path.startsWith(other);
	}

	@Override
	public boolean endsWith(Path other)
	{
		return path.endsWith(other.toString());
	}

	@Override
	public boolean endsWith(String other)
	{
		return path.endsWith(other);
	}

	@Override
	public Path normalize()
	{
		return this;
	}

	@Override
	public Path resolve(Path other)
	{
		return new DbxPath(appendFileName(other.toString()), fileSystem);
	}

	@Override
	public Path resolve(String other)
	{
		return resolve(new DbxPath(other, fileSystem));
	}

	@Override
	public Path resolveSibling(Path other)
	{
		return new DbxPath(reduceAndAppend(other), fileSystem);
	}

	@Override
	public Path resolveSibling(String other)
	{
		return resolveSibling(new DbxPath(other, fileSystem));
	}

	@Override
	public Path relativize(Path other)
	{
		// TODO
		return null;
	}

	@Override
	public URI toUri()
	{
		String path = this.path.replace(" ", "%20");
		return URI.create("dropbox://" + path);
	}

	@Override
	public Path toAbsolutePath()
	{
		return this;
	}

	@Override
	public Path toRealPath(LinkOption... options)
	{
		// TODO
		return null;
	}

	@Override
	public String toString()
	{
		return path;
	}

	@Override
	public File toFile()
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>[] events, Modifier... modifiers)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public WatchKey register(WatchService watcher, Kind<?>... events)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public Iterator<Path> iterator()
	{
		return new Iterator<Path>()
		{

			private int i = 0;

			@Override
			public boolean hasNext()
			{
				return i < getNameCount();
			}

			@Override
			public Path next()
			{
				if(i < getNameCount())
				{
					Path result = getName(i);
					i++;
					return result;
				}
				else
				{
					throw new NoSuchElementException();
				}
			}
		};
	}

	@Override
	public int compareTo(Path other)
	{
		return ((other instanceof DbxPath) && (other.toString().compareTo(path) == 0)) ? 0 : 1;
	}

}
