package de.thecodelabs.versionizer.service.impl;

import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.versionizer.model.RemoteFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public class JarUpdateStrategy extends UpdateStrategy
{
	@Override
	public Path getUpdaterPath()
	{
		return ApplicationUtils.getSharedApplication().getPath(PathType.DOWNLOAD, "Versionizer.jar");
	}

	@Override
	protected Optional<RemoteFile> getSuitableRemoteFile(List<RemoteFile> remoteFiles)
	{
		return remoteFiles.stream().filter(file -> file.getFileType() == RemoteFile.FileType.JAR).findFirst();
	}
}
