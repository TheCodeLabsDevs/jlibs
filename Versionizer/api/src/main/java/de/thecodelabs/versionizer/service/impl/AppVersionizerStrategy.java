package de.thecodelabs.versionizer.service.impl;

import com.google.gson.Gson;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.versionizer.UpdateItem;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.service.UpdateService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class AppVersionizerStrategy extends VersionizerStrategy
{
	@Override
	public Path getUpdaterPath(UpdateService.InteractionType type)
	{
		return ApplicationUtils.getSharedApplication().getPath(PathType.DOWNLOAD, type.name(), "Versionizer.jar");
	}

	@Override
	protected Optional<RemoteFile> getSuitableRemoteFile(List<RemoteFile> remoteFiles)
	{
		return remoteFiles.stream().filter(file -> file.getFileType() == RemoteFile.FileType.JAR).max(Comparator.comparingInt(RemoteFile::getRevision));
	}

	@Override
	public void startVersionizer(UpdateService.InteractionType interactionType, UpdateService.RunPrivileges runPrivileges, UpdateItem updateItem) throws IOException
	{
		Gson gson = new Gson();
		String json = gson.toJson(updateItem);

		final Path updaterPath = getUpdaterPath(interactionType);
		switch(runPrivileges)
		{
			case ADMIN:
			{
				runJar(json, updaterPath);
				break;
			}
			case USER:
			{
				runJar(json, updaterPath);
				break;
			}
		}
	}

	private void runJar(String json, Path updaterPath) throws IOException
	{
		exec(Arrays.asList("java", "-jar", updaterPath.toAbsolutePath().toString()), json);
	}
}
