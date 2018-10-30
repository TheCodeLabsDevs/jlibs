package de.thecodelabs.versionizer.service.impl;

import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.service.UpdateService;

import java.nio.file.Path;
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
}
