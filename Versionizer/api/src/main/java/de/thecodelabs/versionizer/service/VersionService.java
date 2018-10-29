package de.thecodelabs.versionizer.service;

import de.thecodelabs.utils.io.IOUtils;
import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import org.jfrog.artifactory.client.Artifactory;
import org.jfrog.artifactory.client.ArtifactoryClientBuilder;
import org.jfrog.artifactory.client.DownloadableArtifact;
import org.jfrog.artifactory.client.model.File;
import org.jfrog.artifactory.client.model.Folder;
import org.jfrog.artifactory.client.model.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	public static final Logger LOGGER = LoggerFactory.getLogger(VersionService.class);

	private VersionizerItem versionizerItem;
	private Artifactory artifactory;

	public VersionService(VersionizerItem versionizerItem)
	{
		this.versionizerItem = versionizerItem;

		artifactory = ArtifactoryClientBuilder.create()
				.setUrl(versionizerItem.getRepository().getUrl())
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

	public List<Version> getVersions(Artifact build)
	{
		List<Version> versionList = new LinkedList<>();
		try
		{
			versionList.addAll(getVersionsByRepository(artifactory, versionizerItem.getRepository().getRepositoryNameReleases(), build));
		}
		catch(Exception e)
		{
			LOGGER.info("No release versions found for artifact: " + build);
		}
		try
		{
			versionList.addAll(getVersionsByRepository(artifactory, versionizerItem.getRepository().getRepositoryNameSnapshots(), build));
		}
		catch(Exception e)
		{
			LOGGER.info("No snapshot versions found for artifact: " + build);
		}
		versionList.sort(Version::compareTo);

		return versionList;
	}

	public Version getLatestVersion(Artifact build)
	{
		final List<Version> versions = getVersions(build);
		return versions.get(versions.size() - 1);
	}

	public boolean isUpdateAvailable(Artifact build)
	{
		final Version latestVersion = getLatestVersion(build);
		final Version localVersion = VersionTokenizer.getVersion(build, build.getVersion());
		return latestVersion.isNewerTo(localVersion);
	}

	private List<Version> getVersionsByRepository(Artifactory artifactory, String repository, Artifact build)
	{
		final Folder folder = artifactory.repository(repository)
				.folder(build.getGroupId() + "/" + build.getArtifactId())
				.info();

		List<Version> versionList = new LinkedList<>();
		for(Item child : folder.getChildren())
		{
			if(child.isFolder())
			{
				final Version version = VersionTokenizer.getVersion(build, child.getName());
				versionList.add(version);
			}
		}
		return versionList;
	}

	public List<RemoteFile> listFilesForVersion(Version version)
	{
		final Artifact build = version.getArtifact();
		List<RemoteFile> remoteFiles = new ArrayList<>();

		final String repositoryPath = versionizerItem.getRepository().getRepository(version.isSnapshot());
		final String folderPath = build.getGroupId() + "/" + build.getArtifactId() + "/" + version.toVersionString();

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

	public long getSize(RemoteFile remoteFile)
	{
		final String repository = versionizerItem.getRepository().getRepository(remoteFile.getVersion().isSnapshot());
		final File fileInfo = artifactory
				.repository(repository)
				.file(remoteFile.getPath())
				.info();
		return fileInfo.getSize();
	}

	public void downloadRemoteFile(RemoteFile remoteFile, Path destination) throws IOException
	{
		downloadRemoteFile(remoteFile, destination, null);
	}

	public void downloadRemoteFile(RemoteFile remoteFile, Path destination, IOUtils.CopyDelegate copyDelegate) throws IOException
	{
		final String repository = versionizerItem.getRepository().getRepository(remoteFile.getVersion().isSnapshot());
		final DownloadableArtifact downloadableArtifact = artifactory
				.repository(repository)
				.download(remoteFile.getPath());

		final InputStream iStr = downloadableArtifact.doDownload();
		final OutputStream oStr = Files.newOutputStream(destination);

		IOUtils.copy(iStr, oStr, copyDelegate);
	}
}
