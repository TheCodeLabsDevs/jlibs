package de.thecodelabs.artifactory;

import java.util.Date;
import java.util.List;

public class Folder
{
	private String uri;
	private String repo;
	private String path;
	private Date created;
	private String createdBy;
	private Date lastModified;
	private String modifiedBy;
	private Date lastUpdated;
	private List<FolderItem> children;

	public Folder()
	{
	}

	public Folder(String uri, String repo, String path, Date created, String createdBy, Date lastModified, String modifiedBy, Date lastUpdated, List<FolderItem> children)
	{
		this.uri = uri;
		this.repo = repo;
		this.path = path;
		this.created = created;
		this.createdBy = createdBy;
		this.lastModified = lastModified;
		this.modifiedBy = modifiedBy;
		this.lastUpdated = lastUpdated;
		this.children = children;
	}

	public String getUri()
	{
		return uri;
	}

	public String getRepo()
	{
		return repo;
	}

	public String getPath()
	{
		return path;
	}

	public Date getCreated()
	{
		return created;
	}

	public String getCreatedBy()
	{
		return createdBy;
	}

	public Date getLastModified()
	{
		return lastModified;
	}

	public String getModifiedBy()
	{
		return modifiedBy;
	}

	public Date getLastUpdated()
	{
		return lastUpdated;
	}

	public List<FolderItem> getChildren()
	{
		return children;
	}

	@Override
	public String toString()
	{
		return "Folder{" +
				"uri='" + uri + '\'' +
				", repo='" + repo + '\'' +
				", path='" + path + '\'' +
				", created=" + created +
				", createdBy='" + createdBy + '\'' +
				", lastModified=" + lastModified +
				", modifiedBy='" + modifiedBy + '\'' +
				", lastUpdated=" + lastUpdated +
				", children=" + children +
				'}';
	}
}
