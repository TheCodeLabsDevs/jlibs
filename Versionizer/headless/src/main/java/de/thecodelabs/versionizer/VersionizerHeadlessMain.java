package de.thecodelabs.versionizer;

import com.google.gson.Gson;
import de.thecodelabs.logger.FileOutputOption;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.util.StringUtils;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.service.UpdateService;
import de.thecodelabs.versionizer.service.VersionService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class VersionizerHeadlessMain
{
	public static void main(String[] args)
	{
		App app = ApplicationUtils.registerMainApplication(VersionizerHeadlessMain.class);
		ApplicationUtils.addAppListener(VersionizerHeadlessMain::applicationWillStart);
		app.start(args);

		final UpdateItem updateItem;

		final Gson gson = new Gson();
		final Class<UpdateItem> type = UpdateItem.class;

		if(args.length > 0)
		{
			String input = StringUtils.build(args);
			final String json = input.replace("$$", "\"");
			updateItem = gson.fromJson(json, type);
		}
		else
		{
			updateItem = gson.fromJson(new InputStreamReader(System.in), type);
		}

		VersionService versionService = new VersionService(updateItem.getVersionizerItem().getRepository(), UpdateService.RepositoryType.ALL);

		for(UpdateItem.Entry entry : updateItem.getEntryList())
		{
			final Artifact artifact = entry.getVersion().getArtifact();

			Logger.info("Search files for entry: {0}", artifact);
			final List<RemoteFile> remoteFiles = versionService.listFilesForVersion(entry.getVersion());
			final Optional<RemoteFile> optionalRemoteFile = remoteFiles.stream().filter(file -> file.getFileType() == entry.getFileType()).findAny();
			if(!optionalRemoteFile.isPresent())
			{
				Logger.warning("No remote file found for entry: {0}", artifact);
				continue;
			}

			RemoteFile remoteFile = optionalRemoteFile.get();
			Logger.info("Start downloading remote file: {0}", remoteFile);

			final Path downloadPath = app.getPath(PathType.DOWNLOAD, remoteFile.getPath());
			try
			{
				versionService.downloadRemoteFile(remoteFile, downloadPath);
				Logger.info("Download completed for remote file {0}", entry);

				Logger.info("Copy remote file from {0} to {1}", downloadPath, artifact.getLocalPath());
				Files.copy(downloadPath, artifact.getLocalPath(), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(IOException e)
			{
				Logger.error(e);
			}
		}
		handlePostExecution(updateItem);
	}

	private static void handlePostExecution(UpdateItem updateItem)
	{
		String executePath = updateItem.getVersionizerItem().getExecutablePath();
		if(executePath != null)
		{
			Logger.info("Handling execution path: {0}", executePath);
			try
			{
				ProcessBuilder builder = new ProcessBuilder("java", "-jar", executePath);
				builder.start();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private static void applicationWillStart(App app)
	{
		Logger.init(ApplicationUtils.getApplication().getPath(PathType.LOG));
		Logger.setFileOutput(FileOutputOption.COMBINED);
	}
}
