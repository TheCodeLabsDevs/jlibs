package de.thecodelabs.artifactory;

import java.util.Date;
import java.util.Map;

public class File
{
	private String uri;
	private String repo;
	private String path;
	private Date created;
	private String createdBy;
	private Date lastModified;
	private String modifiedBy;
	private Date lastUpdated;
	private String downloadUri;
	private String mimeType;
	private Long size;
	private Map<String, String> checksums;
	private Map<String, String> originalChecksums;

	public File()
	{
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

	public String getDownloadUri()
	{
		return downloadUri;
	}

	public String getMimeType()
	{
		return mimeType;
	}

	public Long getSize()
	{
		return size;
	}

	public Map<String, String> getChecksums()
	{
		return checksums;
	}

	public Map<String, String> getOriginalChecksums()
	{
		return originalChecksums;
	}

	@Override
	public String toString()
	{
		return "File{" +
				"uri='" + uri + '\'' +
				", repo='" + repo + '\'' +
				", path='" + path + '\'' +
				", created=" + created +
				", createdBy='" + createdBy + '\'' +
				", lastModified=" + lastModified +
				", modifiedBy='" + modifiedBy + '\'' +
				", lastUpdated=" + lastUpdated +
				", downloadUri='" + downloadUri + '\'' +
				", mimeType='" + mimeType + '\'' +
				", size=" + size +
				", checksums=" + checksums +
				", originalChecksums=" + originalChecksums +
				'}';
	}
}
