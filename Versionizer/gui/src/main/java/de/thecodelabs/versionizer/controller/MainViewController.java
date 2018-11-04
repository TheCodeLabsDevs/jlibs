package de.thecodelabs.versionizer.controller;

import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.threading.Worker;
import de.thecodelabs.utils.ui.NVC;
import de.thecodelabs.utils.util.NumberUtils;
import de.thecodelabs.versionizer.UpdateItem;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.service.VersionService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

public class MainViewController extends NVC
{
	@FXML
	private Label itemLabel;
	@FXML
	private Label progressLabel;
	@FXML
	private ProgressBar progressBar;
	@FXML
	private Button cancelButton;

	private UpdateItem updateItem;

	public MainViewController(Stage stage, UpdateItem item)
	{
		this.updateItem = item;
		load("view", "Main");
		applyViewControllerToStage(stage);

		Worker.runLater(this::runUpdateInBackground);
	}

	@Override
	public void init()
	{
		super.init();
	}

	@FXML
	void onCancelHandler(ActionEvent event)
	{

	}

	private void runUpdateInBackground()
	{
		App app = ApplicationUtils.getApplication();
		VersionService versionService = new VersionService(updateItem.getVersionizerItem());

		for(UpdateItem.Entry entry : updateItem.getEntryList())
		{

			Logger.info("Search files for entry: {0}", entry.getVersion().getArtifact());
			Platform.runLater(() -> itemLabel.setText(entry.getVersion().getArtifact().getArtifactId()));

			final List<RemoteFile> remoteFiles = versionService.listFilesForVersion(entry.getVersion());
			final Optional<RemoteFile> optionalRemoteFile = remoteFiles.stream().filter(file -> file.getFileType() == entry.getFileType()).findAny();
			if(!optionalRemoteFile.isPresent())
			{
				Logger.warning("No remote file found for entry: {0}", entry.getVersion().getArtifact());
				continue;
			}

			RemoteFile remoteFile = optionalRemoteFile.get();
			Logger.info("Start download remote file: {0}", remoteFile);

			final Path downloadPath = app.getPath(PathType.DOWNLOAD, remoteFile.getPath());
			try
			{
				final long maxSize = versionService.getSize(remoteFile);
				versionService.downloadRemoteFile(remoteFile, downloadPath, (value) -> Platform.runLater(() -> setCurrentProgress(value, maxSize)));
				Logger.info("Download completed for remote file {0}", entry);

				Logger.info("Copy remote file from {0} to {1}", downloadPath, entry.getLocalPath());
				Files.copy(downloadPath, Paths.get(entry.getLocalPath()), StandardCopyOption.REPLACE_EXISTING);
			}
			catch(IOException e)
			{
				Logger.error(e);
			}
		}
		String executePath = updateItem.getVersionizerItem().getExecutablePath();
		if(executePath != null)
		{
			Logger.info("Handling execution path: {0}", executePath);
			try
			{
				if(executePath.toLowerCase().endsWith(".jar"))
				{
					ProcessBuilder builder = new ProcessBuilder("java", "-jar", executePath);
					builder.start();
				}
				else
				{
					Desktop.getDesktop().open(new File(executePath));
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

	private void setCurrentProgress(long value, long max)
	{
		double progress = (double) value / (double) max;
		progressBar.setProgress(progress);
		progressLabel.setText(String.format("%sB / %sB", NumberUtils.convertBytesToAppropriateFormat(value), NumberUtils.convertBytesToAppropriateFormat(max)));
	}
}
