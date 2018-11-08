package de.thecodelabs.versionizer;

import com.google.gson.Gson;
import de.thecodelabs.logger.FileOutputOption;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.service.VersionService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
			final String json = args[0];
			updateItem = gson.fromJson(json, type);
		}
		else
		{
			updateItem = gson.fromJson(new InputStreamReader(System.in), type);
		}

		VersionService versionService = new VersionService(updateItem.getVersionizerItem());

		for(UpdateItem.Entry entry : updateItem.getEntryList())
		{
			Logger.info("Search files for entry: {0}", entry.getVersion().getArtifact());
			final List<RemoteFile> remoteFiles = versionService.listFilesForVersion(entry.getVersion());
			final Optional<RemoteFile> optionalRemoteFile = remoteFiles.stream().filter(file -> file.getFileType() == entry.getFileType()).findAny();
			if(!optionalRemoteFile.isPresent())
			{
				Logger.warning("No remote file found for entry: {0}", entry.getVersion().getArtifact());
				continue;
			}

			RemoteFile remoteFile = optionalRemoteFile.get();
			Logger.info("Start downloading remote file: {0}", remoteFile);

			final Path downloadPath = app.getPath(PathType.DOWNLOAD, remoteFile.getPath());
			try
			{
				versionService.downloadRemoteFile(remoteFile, downloadPath);
				Logger.info("Download completed for remote file {0}", entry);

				Logger.info("Copy remote file from {0} to {1}", downloadPath, entry.getLocalPath());
				Files.copy(downloadPath, Paths.get(entry.getLocalPath()), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(IOException e)
			{
				Logger.error(e);
			}
		}
	}

	private static void applicationWillStart(App app)
	{
		Logger.init(ApplicationUtils.getApplication().getPath(PathType.LOG));
		Logger.setFileOutput(FileOutputOption.COMBINED);
	}
}
