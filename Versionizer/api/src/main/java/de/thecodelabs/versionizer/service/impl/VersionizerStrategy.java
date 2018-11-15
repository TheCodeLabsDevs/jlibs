package de.thecodelabs.versionizer.service.impl;

import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.external.ExternalJarContainer;
import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.versionizer.UpdateItem;
import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.config.Repository;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.UpdateService;
import de.thecodelabs.versionizer.service.VersionService;
import de.thecodelabs.versionizer.service.VersionTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class VersionizerStrategy
{

	public static final Logger LOGGER = LoggerFactory.getLogger(UpdateService.class);

	public abstract Path getUpdaterPath(UpdateService.InteractionType type);

	protected abstract Optional<RemoteFile> getSuitableRemoteFile(List<RemoteFile> remoteFiles);

	public abstract void startVersionizer(UpdateService.InteractionType interactionType, UpdateService.RunPrivileges runPrivileges, UpdateItem updateItem) throws IOException;

	public void downloadVersionizer(UpdateService.InteractionType interactionType) throws IOException
	{
		if(!isVersionizerInstalled(interactionType) || isVersionizerUpdateAvailable(interactionType))
		{
			downloadVersionizerFile(interactionType);
		}
	}

	private boolean isVersionizerInstalled(UpdateService.InteractionType type)
	{
		return Files.exists(getUpdaterPath(type));
	}

	private boolean isVersionizerUpdateAvailable(UpdateService.InteractionType type)
	{
		final Path versionizerPath = getUpdaterPath(type);
		if(Files.notExists(versionizerPath))
		{
			return false;
		}

		try
		{
			ExternalJarContainer container = ExternalJarContainer.getExternalJar(versionizerPath);
			Repository repository = container.get("versionizer/repository.yml").deserialize(StorageTypes.YAML, Repository.class);
			Artifact artifact = container.get("versionizer/build.properties").deserialize(StorageTypes.PROPERTIES, Artifact.class);
			VersionizerItem versionizerItem = new VersionizerItem(repository, Collections.singletonList(artifact), null);

			VersionService versionService = new VersionService(versionizerItem);
			final Version latestVersion = versionService.getLatestVersion(artifact);

			container.close();
			versionService.close();

			System.out.println("Remote: " + latestVersion);
			System.out.println("Local: " + VersionTokenizer.getVersion(artifact));

			return latestVersion.isNewerThen(VersionTokenizer.getVersion(artifact));
		} catch (RuntimeException e) {
			LoggerBridge.warning(e.getMessage());
			return false;
		}
	}

	private void downloadVersionizerFile(UpdateService.InteractionType type) throws IOException
	{
		final Path versionizerPath = getUpdaterPath(type);
		Files.createDirectories(versionizerPath.getParent());

		Repository repository;
		Artifact build;

		App app = ApplicationUtils.getApplication();
		repository = app.getClasspathResource("versionizer/repository.yml").deserialize(StorageTypes.YAML, Repository.class);
		build = app.getClasspathResource("versionizer/" + type + "-build.properties").deserialize(StorageTypes.PROPERTIES, Artifact.class);

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
		versionService.close();
	}
}
