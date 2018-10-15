package de.thecodelabs.versionizer.service;

import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.model.VersionTokenizer;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.jfrog.artifactory.client.model.Folder;
import org.jfrog.artifactory.client.model.Item;

import java.util.ArrayList;
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

	public List<RemoteFile> listFilesForVersion(VersionizerItem item, Version version)
	{
		List<RemoteFile> remoteFiles = new ArrayList<>();

		Artifactory artifactory = ArtifactoryClientBuilder.create()
				.setUrl(item.getArtifactoryUrl())
				.build();

		final String repositoryPath = item.getRepository(version.isSnapshot());
		final String folderPath = item.getGroupId() + "/" + item.getArtifactId() + "/" + version.toVersionString();

		final Folder folder = artifactory
				.repository(repositoryPath)
				.folder(folderPath)
				.info();

		for(Item child : folder.getChildren())
		{
			RemoteFile.FileType extension = RemoteFile.FileType.getFileType(child.getName());
			if(extension != null)
			{
				final String path = repositoryPath + "/" + folderPath + child.getUri();
				RemoteFile remoteFile = new RemoteFile(child.getName(), path, extension);
				remoteFiles.add(remoteFile);
			}
		}

		return remoteFiles;
	}
}
