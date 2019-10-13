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

	public File(String uri, String repo, String path, Date created, String createdBy, Date lastModified, String modifiedBy, Date lastUpdated, String downloadUri, String mimeType, Long size, Map<String, String> checksums, Map<String, String> originalChecksums)
	{
		this.uri = uri;
		this.repo = repo;
		this.path = path;
		this.created = created;
		this.createdBy = createdBy;
		this.lastModified = lastModified;
		this.modifiedBy = modifiedBy;
		this.lastUpdated = lastUpdated;
		this.downloadUri = downloadUri;
		this.mimeType = mimeType;
		this.size = size;
		this.checksums = checksums;
		this.originalChecksums = originalChecksums;
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
