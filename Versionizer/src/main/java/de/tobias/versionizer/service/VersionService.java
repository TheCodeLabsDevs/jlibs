package de.tobias.versionizer.service;

import de.tobias.versionizer.VersionizerItem;
import de.tobias.versionizer.model.Version;
import de.tobias.versionizer.model.VersionTokenizer;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.jfrog.artifactory.client.model.Folder;
import org.jfrog.artifactory.client.model.Item;

import java.util.LinkedList;
import java.util.List;

public class VersionService
{
	public Version[] getVersions(VersionizerItem item)
	{
		Artifactory artifactory = ArtifactoryClientBuilder.create()
				.setUrl(item.getArtifactoryUrl())
				.build();

		List<Version> versionList = new LinkedList<>();
		versionList.addAll(getVersionsByRepository(artifactory, item, item.getReleaseRepository()));
		versionList.addAll(getVersionsByRepository(artifactory, item, item.getSnapshotRepository()));

		artifactory.close();

		return versionList.toArray(new Version[0]);
	}

	private List<Version> getVersionsByRepository(Artifactory artifactory, VersionizerItem item, String repository)
	{
		final Folder folder = artifactory.repository(repository)
				.folder(item.getGroupId() + "/" + item.getArtifactId())
				.info();

		List<Version> versionList = new LinkedList<>();
		for(Item child : folder.getChildren())
		{
			if(child.isFolder())
			{
				final Version version = VersionTokenizer.getVersion(child.getName());
				versionList.add(version);
			}
		}
		return versionList;
	}

	public void listFilesForVersion(VersionizerItem item, Version version)
	{
		Artifactory artifactory = ArtifactoryClientBuilder.create()
				.setUrl(item.getArtifactoryUrl())
				.build();

		final Folder folder = artifactory.repository(item.getSnapshotRepository())
				.folder(item.getGroupId() + "/" + item.getArtifactId() + "/" + version.toVersionString()).info();

		for(Item child : folder.getChildren())
		{
			System.out.println("\t" + child.getName());
		}
	}
}
