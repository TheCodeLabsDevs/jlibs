package de.thecodelabs.versionizer.service;

import de.thecodelabs.artifactory.Artifactory;
import de.thecodelabs.artifactory.File;
import de.thecodelabs.artifactory.Folder;
import de.thecodelabs.artifactory.FolderItem;
import de.thecodelabs.utils.io.IOUtils;
import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.config.Repository;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;

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
	private final Repository repository;
	private final Artifactory artifactory;
	private UpdateService.RepositoryType repositoryType;

	public VersionService(Repository repository, UpdateService.RepositoryType repositoryType)
	{
		this.repository = repository;
		this.repositoryType = repositoryType;

		artifactory = new Artifactory(repository.getUrl());
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
			LoggerBridge.info("No release versions found for artifact: " + build);
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
			LoggerBridge.info("No snapshot versions found for artifact: " + build);
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
			LoggerBridge.warning("No Versions found");
			return false;
		}

		final Version localVersion = VersionTokenizer.getVersion(build, build.getVersion());
		return latestVersion.isNewerThen(localVersion);
	}

	private List<Version> getVersionsByRepository(Artifactory artifactory, String repository, Artifact build)
	{
		final Folder folder = artifactory.getRepository(repository)
				.getFolder(build.getGroupId() + "/" + build.getArtifactId());

		List<Version> versionList = new LinkedList<>();
		for(FolderItem child : folder.getChildren())
		{
			if(child.isFolder())
			{
				final Version version = VersionTokenizer.getVersion(build, child.getUri());
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
				.getRepository(repositoryPath)
				.getFolder(folderPath);

		for(FolderItem child : folder.getChildren())
		{
			RemoteFile.FileType extension = RemoteFile.FileType.getFileType(child.getUri());
			if(extension != null)
			{
				final String path = folderPath + child.getUri();
				RemoteFile remoteFile = new RemoteFile(version, child.getUri(), path, extension);
				remoteFiles.add(remoteFile);
			}
		}

		return remoteFiles;
	}

	public long getSize(RemoteFile remoteFile)
	{
		final String url = this.repository.getRepository(remoteFile.getVersion().isSnapshot());
		final File fileInfo = artifactory
				.getRepository(url)
				.getFile(remoteFile.getPath());
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
		final InputStream inputStream = artifactory
				.getRepository(url)
				.download(remoteFile.getPath());

		final OutputStream oStr = Files.newOutputStream(destination);

		IOUtils.copy(inputStream, oStr, copyDelegate, copyControl);

		inputStream.close();
		oStr.close();
	}

	public void setRepositoryType(UpdateService.RepositoryType repositoryType)
	{
		this.repositoryType = repositoryType;
	}
}
