package de.thecodelabs.versionizer.service.impl;

import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.external.ExternalJarContainer;
import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.config.Repository;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.UpdateService;
import de.thecodelabs.versionizer.service.VersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class UpdateStrategy
{

	public static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);

	public abstract Path getUpdaterPath();

	protected abstract Optional<RemoteFile> getSuitableRemoteFile(List<RemoteFile> remoteFiles);

	public void downloadVersionizer() throws IOException
	{
		if(!isVersionizerInstalled() || isUpdateAvailable())
		{
			download();
		}
	}

	private boolean isVersionizerInstalled()
	{
		return Files.exists(getUpdaterPath());
	}

	private boolean isUpdateAvailable()
	{
		final Path versionizerPath = getUpdaterPath();
		if (Files.notExists(versionizerPath)) {
			return false;
		}
		ExternalJarContainer container = ExternalJarContainer.getExternalJar(versionizerPath);
		Repository repository = container.get("versionizer/repository.yml").deserialize(StorageTypes.YAML, Repository.class);
		Artifact artifact = container.get("versionizer/build.properties").deserialize(StorageTypes.PROPERTIES, Artifact.class);

		container.close();

		return isUpdateAvailableForArtifact(repository, artifact);
	}

	private void download() throws IOException
	{
		final Path versionizerPath = getUpdaterPath();

		Repository repository;
		Artifact build;

		App app = ApplicationUtils.getApplication();
		repository = app.getClasspathResource("versionizer/repository.yml").deserialize(StorageTypes.YAML, Repository.class);
		build = app.getClasspathResource("versionizer/build.properties").deserialize(StorageTypes.PROPERTIES, Artifact.class);

		LOGGER.info("Downloading versionizer using artifact" + build);

		VersionizerItem versionizerItem = new VersionizerItem(repository, Collections.singletonList(build), versionizerPath.toString());
		VersionService versionService = new VersionService(versionizerItem);

		final Version latestVersion = versionService.getLatestVersion(build);
		final List<RemoteFile> remoteFiles = versionService.listFilesForVersion(latestVersion);
		Optional<RemoteFile> remoteFile = getSuitableRemoteFile(remoteFiles);
		if(remoteFile.isPresent())
		{
			versionService.downloadRemoteFile(remoteFile.get(), versionizerPath);
		}
		else
		{
			throw new FileNotFoundException(
					"Versionizer file not found on artifactory"
			);
		}
	}

	public boolean isUpdateAvailableForArtifact(Repository repository, Artifact artifact)
	{
		VersionizerItem versionizerItem = new VersionizerItem(repository, Collections.singletonList(artifact), null);
		VersionService versionService = new VersionService(versionizerItem);
		return versionService.isUpdateAvailable(artifact);
	}
}
