package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;

import java.util.List;

public class UpdateItem
{
	public static class Entry
	{
		private Version version;
		private String localPath;
		private RemoteFile.FileType fileType;

		public Entry(Version version, String localPath, RemoteFile.FileType fileType)
		{
			this.version = version;
			this.localPath = localPath;
			this.fileType = fileType;
		}

		public Version getVersion()
		{
			return version;
		}

		public String getLocalPath()
		{
			return localPath;
		}

		public RemoteFile.FileType getFileType()
		{
			return fileType;
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
}
