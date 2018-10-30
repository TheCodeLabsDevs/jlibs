package de.thecodelabs.versionizer.service.impl;

import com.google.gson.Gson;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.application.system.NativeApplication;
import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.service.UpdateService;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class ExeVersionizerStrategy extends VersionizerStrategy
{
	@Override
	public Path getUpdaterPath(UpdateService.InteractionType type)
	{
		return ApplicationUtils.getSharedApplication().getPath(PathType.DOWNLOAD, type.name(), "Versionizer.exe");
	}

	@Override
	protected Optional<RemoteFile> getSuitableRemoteFile(List<RemoteFile> remoteFiles)
	{
		return remoteFiles.stream().filter(file -> file.getFileType() == RemoteFile.FileType.EXE).max(Comparator.comparingInt(RemoteFile::getRevision));
	}

	@Override
	public void startVersionizer(UpdateService.InteractionType interactionType, UpdateService.RunPrivileges runPrivileges, VersionizerItem versionizerItem) throws IOException
	{
		Gson gson = new Gson();
		String json = gson.toJson(versionizerItem);

		final Path updaterPath = getUpdaterPath(interactionType);
		switch(runPrivileges)
		{
			case ADMIN:
			{
				NativeApplication.sharedInstance().executeAsAdministrator(updaterPath.toAbsolutePath().toString(), json);
			}
			break;
			case USER:
			{
				ProcessBuilder builder = new ProcessBuilder(updaterPath.toAbsolutePath().toString(), json);
				builder.start();
			}
			break;
		}
	}
}
