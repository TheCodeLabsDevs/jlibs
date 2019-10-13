package de.thecodelabs.versionizer.model;

import de.thecodelabs.utils.io.PathUtils;
import de.thecodelabs.versionizer.service.VersionTokenizer;

import java.util.Objects;

public class RemoteFile implements Comparable<RemoteFile>
{
	public enum FileType
	{
		JAR("jar"),
		EXE("exe"),
		ZIP("zip");

		private String fileExtension;

		FileType(String fileExtension)
		{
			this.fileExtension = fileExtension;
		}

		public String getFileExtension()
		{
			return fileExtension;
		}

		public static FileType getFileType(String fileName)
		{
			String fileExtension = PathUtils.getFileExtension(fileName);
			for(FileType value : values())
			{
				if(value.getFileExtension().equalsIgnoreCase(fileExtension))
				{
					return value;
				}
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
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof RemoteFile)) return false;
		RemoteFile that = (RemoteFile) o;
		return revision == that.revision &&
				Objects.equals(version, that.version) &&
				Objects.equals(name, that.name) &&
				Objects.equals(path, that.path) &&
				fileType == that.fileType;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(version, name, path, fileType, revision);
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
