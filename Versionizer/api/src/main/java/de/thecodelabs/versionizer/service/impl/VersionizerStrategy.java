package de.thecodelabs.versionizer.service.impl;

import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.resources.external.ExternalJarContainer;
import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.versionizer.UpdateItem;
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
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class VersionizerStrategy
{

	public static final Logger LOGGER = LoggerFactory.getLogger(VersionizerStrategy.class);

	public abstract Path getUpdaterPath(UpdateService.InteractionType type);

	protected abstract Optional<RemoteFile> getSuitableRemoteFile(List<RemoteFile> remoteFiles);

	public abstract void startVersionizer(UpdateService.InteractionType interactionType,
										  UpdateService.RunPrivileges runPrivileges,
										  UpdateItem updateItem) throws IOException;

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
			Artifact artifact = container.get("versionizer/build.json").deserialize(StorageTypes.JSON, Artifact.class);

			VersionService versionService = new VersionService(repository, UpdateService.RepositoryType.RELEASE);
			final Version remoteVersion = versionService.getLatestVersion(artifact);
			final Version localVersion = VersionTokenizer.getVersion(artifact);

			container.close();

			LOGGER.debug("Versionizer Remote: {}", remoteVersion);
			LOGGER.debug("Versionizer Local: {}", localVersion);

			return remoteVersion.isNewerThen(localVersion);
		}
		catch(RuntimeException e)
		{
			LOGGER.warn(e.getMessage());
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
		build = app.getClasspathResource("versionizer/" + type + "-build.json").deserialize(StorageTypes.JSON, Artifact.class);

		LOGGER.info("Downloading versionizer using artifact {}", build);

		VersionService versionService = new VersionService(repository, UpdateService.RepositoryType.RELEASE);

		final Version latestVersion = versionService.getLatestVersion(build);
		final List<RemoteFile> remoteFiles = versionService.listFilesForVersion(latestVersion);
		Optional<RemoteFile> remoteFile = getSuitableRemoteFile(remoteFiles);
		if(remoteFile.isPresent())
		{
			versionService.downloadRemoteFile(remoteFile.get(), versionizerPath);
		}
		else
		{
			throw new FileNotFoundException("Versionizer file not found on artifactory");
		}
	}

	protected void exec(String path, String json) throws IOException
	{
		exec(Collections.singletonList(path), json);
	}

	protected void exec(List<String> command, String json) throws IOException
	{
		LoggerBridge.debug("Exec: " + command);

		ProcessBuilder builder = new ProcessBuilder(command);
		final Process start = builder.start();

		final OutputStream outputStream = start.getOutputStream();
		outputStream.write(json.getBytes());
		outputStream.flush();
		outputStream.close();
	}
}
