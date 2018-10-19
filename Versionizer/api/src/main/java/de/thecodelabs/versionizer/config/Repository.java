package de.thecodelabs.versionizer.config;

import de.thecodelabs.storage.settings.annotation.Key;
import de.thecodelabs.storage.settings.annotation.Required;

public class Repository
{
	@Key
	@Required
	private String url;
	@Key("repository.releases")
	@Required
	private String repositoryNameReleases;
	@Key("repository.snapshots")
	private String repositoryNameSnapshots;

	public String getUrl()
	{
		return url;
	}

	public String getRepositoryNameReleases()
	{
		return repositoryNameReleases;
	}

	public String getRepositoryNameSnapshots()
	{
		return repositoryNameSnapshots;
	}

	public String getRepository(boolean snapshot) {
		if (snapshot) {
			return repositoryNameSnapshots;
		} else {
			return repositoryNameReleases;
		}
	}
}
