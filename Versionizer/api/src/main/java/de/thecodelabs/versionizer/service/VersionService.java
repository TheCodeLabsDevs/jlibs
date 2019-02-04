package de.thecodelabs.versionizer.service;

import de.thecodelabs.utils.io.IOUtils;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.config.Repository;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(VersionService.class);

	private Repository repository;
	private Artifactory artifactory;
	private UpdateService.RepositoryType repositoryType;

	public VersionService(Repository repository, UpdateService.RepositoryType repositoryType)
	{
		this.repository = repository;
		this.repositoryType = repositoryType;

		artifactory = ArtifactoryClientBuilder.create()
				.setUrl(repository.getUrl())
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

	public List<Version> getVersionsSorted(Artifact build)
	{
		List<Version> versionList = new LinkedList<>();

		// Release Repository
		try
		{
			if(repositoryType != UpdateService.RepositoryType.SNAPSHOT)
			{
				versionList.addAll(getVersionsByRepository(artifactory, repository.getRepositoryNameReleases(), build));
			}
		}
		catch(Exception e)
		{
			LOGGER.info("No release versions found for artifact: " + build);
		}

		// Snapshot Repository
		try
		{
			if(repositoryType != UpdateService.RepositoryType.RELEASE)
			{
				versionList.addAll(getVersionsByRepository(artifactory, repository.getRepositoryNameSnapshots(), build));
			}
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
		final List<Version> versions = getVersionsSorted(build);
		if(versions.isEmpty())
		{
			return null;
		}
		return versions.get(versions.size() - 1);
	}

	@SuppressWarnings("unused")
	public boolean isUpdateAvailable(Artifact build)
	{
		final Version latestVersion = getLatestVersion(build);
		if(latestVersion == null)
		{
			LOGGER.warn("No Versions found");
			return false;
		}

		final Version localVersion = VersionTokenizer.getVersion(build, build.getVersion());
		return latestVersion.isNewerThen(localVersion);
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

		final String repositoryPath = repository.getRepository(version.isSnapshot());
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
		final String url = this.repository.getRepository(remoteFile.getVersion().isSnapshot());
		final File fileInfo = artifactory
				.repository(url)
				.file(remoteFile.getPath())
				.info();
		return fileInfo.getSize();
	}

	public void downloadRemoteFile(RemoteFile remoteFile, Path destination) throws IOException
	{
		downloadRemoteFile(remoteFile, destination, null);
	}

	@SuppressWarnings("WeakerAccess")
	public void downloadRemoteFile(RemoteFile remoteFile, Path destination, IOUtils.CopyDelegate copyDelegate) throws IOException
	{
		downloadRemoteFile(remoteFile, destination, copyDelegate, null);
	}

	public void downloadRemoteFile(RemoteFile remoteFile,
								   Path destination,
								   IOUtils.CopyDelegate copyDelegate,
								   IOUtils.CopyControl copyControl) throws IOException
	{
		final String url = this.repository.getRepository(remoteFile.getVersion().isSnapshot());
		final DownloadableArtifact downloadableArtifact = artifactory
				.repository(url)
				.download(remoteFile.getPath());

		final InputStream iStr = downloadableArtifact.doDownload();
		final OutputStream oStr = Files.newOutputStream(destination);

		IOUtils.copy(iStr, oStr, copyDelegate, copyControl);

		iStr.close();
		oStr.close();
	}

	public void setRepositoryType(UpdateService.RepositoryType repositoryType)
	{
		this.repositoryType = repositoryType;
	}
}
