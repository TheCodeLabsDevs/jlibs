package de.thecodelabs.versionizer.service;

import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.model.VersionTokenizer;
import de.tobias.utils.io.IOUtils;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.jfrog.artifactory.client.DownloadableArtifact;
import org.jfrog.artifactory.client.model.File;
import org.jfrog.artifactory.client.model.Folder;
import org.jfrog.artifactory.client.model.Item;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VersionService
{
	private VersionizerItem versionizerItem;
	private Artifactory artifactory;

	public VersionService(VersionizerItem versionizerItem)
	{
		this.versionizerItem = versionizerItem;

		artifactory = ArtifactoryClientBuilder.create()
				.setUrl(versionizerItem.getArtifactoryUrl())
				.build();
	}

	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		close();
	}

	public void close()
	{
		if(this.artifactory != null)
		{
			this.artifactory.close();
		}
	}

	public Version[] getVersions()
	{
		List<Version> versionList = new LinkedList<>();
		versionList.addAll(getVersionsByRepository(artifactory, versionizerItem.getReleaseRepository()));
		versionList.addAll(getVersionsByRepository(artifactory, versionizerItem.getSnapshotRepository()));

		return versionList.toArray(new Version[0]);
	}

	private List<Version> getVersionsByRepository(Artifactory artifactory, String repository)
	{
		final Folder folder = artifactory.repository(repository)
				.folder(versionizerItem.getGroupId() + "/" + versionizerItem.getArtifactId())
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

	public List<RemoteFile> listFilesForVersion(Version version)
	{
		List<RemoteFile> remoteFiles = new ArrayList<>();

		final String repositoryPath = versionizerItem.getRepository(version.isSnapshot());
		final String folderPath = versionizerItem.getGroupId() + "/" + versionizerItem.getArtifactId() + "/" + version.toVersionString();

		final Folder folder = artifactory
				.repository(repositoryPath)
				.folder(folderPath)
				.info();

		for(Item child : folder.getChildren())
		{
			RemoteFile.FileType extension = RemoteFile.FileType.getFileType(child.getName());
			if(extension != null)
			{
				final String path = folderPath + child.getUri();
				RemoteFile remoteFile = new RemoteFile(version, child.getName(), path, extension);
				remoteFiles.add(remoteFile);
			}
		}

		return remoteFiles;
	}

	public void downloadRemoteFile(RemoteFile remoteFile, Path destination) throws IOException
	{
		final String repository = versionizerItem.getRepository(remoteFile.getVersion().isSnapshot());

		final File fileInfo = artifactory
				.repository(repository)
				.file(remoteFile.getPath())
				.info();

		final DownloadableArtifact downloadableArtifact = artifactory
				.repository(repository)
				.download(remoteFile.getPath());

		final InputStream iStr = downloadableArtifact.doDownload();
		final OutputStream oStr = Files.newOutputStream(destination);

		IOUtils.copy(iStr, oStr, (round) -> System.out.println(round + "/" + fileInfo.getSize()));
	}
}
