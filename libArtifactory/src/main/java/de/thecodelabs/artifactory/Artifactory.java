package de.thecodelabs.artifactory;

public class Artifactory
{
	private String url;

	public Artifactory(String url)
	{
		this.url = url;
	}

	public String getUrl()
	{
		return url;
	}

	public MavenRepository getRepository(String name) {
		return new MavenRepository(this, name);
	}
}
