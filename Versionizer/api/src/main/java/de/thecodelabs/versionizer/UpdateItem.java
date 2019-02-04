package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;

import java.util.List;

public class UpdateItem
{
	public static final int VERSION = 1;

	public static class Entry
	{
		private Version version;
		private RemoteFile.FileType fileType;

		public Entry(Version version)
		{
			this.version = version;
		}

		public Version getVersion()
		{
			return version;
		}

		public RemoteFile.FileType getFileType()
		{
			return fileType;
		}

		public void setFileType(RemoteFile.FileType fileType)
		{
			this.fileType = fileType;
		}

		@Override
		public String toString()
		{
			return "Entry{" +
					"version=" + version +
					", fileType=" + fileType +
					'}';
		}
	}

	private final VersionizerItem versionizerItem;
	private final List<Entry> versionList;

	public UpdateItem(VersionizerItem versionizerItem, List<Entry> versionList)
	{
		this.versionizerItem = versionizerItem;
		this.versionList = versionList;
	}

	public VersionizerItem getVersionizerItem()
	{
		return versionizerItem;
	}

	public List<Entry> getEntryList()
	{
		return versionList;
	}

	@Override
	public String toString()
	{
		return "UpdateItem{" +
				"VERSION=" + VERSION +
				", versionizerItem=" + versionizerItem +
				", versionList=" + versionList +
				'}';
	}
}
