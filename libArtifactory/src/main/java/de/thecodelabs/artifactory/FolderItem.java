package de.thecodelabs.artifactory;

public class FolderItem
{
	private String uri;
	private boolean folder;

	public FolderItem()
	{
	}

	public String getUri()
	{
		return uri;
	}

	public boolean isFolder()
	{
		return folder;
	}

	@Override
	public String toString()
	{
		return "FolderItem{" +
				"uri='" + uri + '\'' +
				", folder=" + folder +
				'}';
	}
}
