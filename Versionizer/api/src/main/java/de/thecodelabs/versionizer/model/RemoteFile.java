package de.thecodelabs.versionizer.model;

import de.thecodelabs.utils.io.PathUtils;

public class RemoteFile implements Comparable<RemoteFile>
{
	public enum FileType
	{
		JAR, EXE, ZIP;

		public static FileType getFileType(String fileName)
		{
			switch(PathUtils.getFileExtension(fileName))
			{
				case "jar":
					return JAR;
				case "zip":
					return ZIP;
				case "exe":
					return EXE;
			}
			return null;
		}
	}

	private final Version version;

	private final String name;
	private final String path;

	private final FileType fileType;
	private final int revision;

	public RemoteFile(Version version, String name, String path, FileType fileType)
	{
		this.version = version;
		this.name = name;
		this.path = path;
		this.fileType = fileType;
		this.revision = VersionTokenizer.getRevision(name);
	}

	public Version getVersion()
	{
		return version;
	}

	public String getName()
	{
		return name;
	}

	public String getPath()
	{
		return path;
	}

	public FileType getFileType()
	{
		return fileType;
	}

	public int getRevision()
	{
		return revision;
	}

	@Override
	public int compareTo(RemoteFile o)
	{
		return Integer.compare(revision, o.revision);
	}

	@Override
	public String toString()
	{
		return "RemoteFile{" +
				"version=" + version +
				", name='" + name + '\'' +
				", path='" + path + '\'' +
				", fileType=" + fileType +
				", revision=" + revision +
				'}';
	}
}
