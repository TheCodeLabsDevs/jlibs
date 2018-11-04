package de.tobias.updater.viewcontroller;

import de.thecodelabs.utils.threading.Worker;
import de.thecodelabs.utils.ui.NVC;
import de.thecodelabs.utils.util.NumberUtils;
import de.tobias.updater.Updater;
import de.tobias.updater.main.UpdateFile;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;
import java.nio.file.Path;
import java.util.List;

public class UpdateViewController extends NVC implements Updater.UpdaterListener {

	@FXML
	private ProgressBar progressBar;
	@FXML
	private Label detailLabel;
	@FXML
	private Label progressLabel;

	private int done = 1;

	private Updater updater;

	public UpdateViewController(Path downloadPath, List<UpdateFile> files, Path executePath) {
		load("view", "UpdateView");
		applyViewControllerToStage();

		updater = new Updater(this);
		startUpdate(downloadPath, files, executePath);
	}

	private void startUpdate(Path downloadPath, List<UpdateFile> files, Path executePath) {
		Worker.runLater(() ->
		{
			for (UpdateFile file : files) {
				try {
					Platform.runLater(() ->
					{
						detailLabel.setText("Update (" + done + "/" + files.size() + "): " + file.getLocalPath().getFileName());
						done++;
					});
					Path downloadFle = updater.downloadFile(downloadPath, file);
					updater.installFile(downloadFle, file);
				} catch (Exception e) {
					e.printStackTrace();
					Platform.runLater(() -> showErrorMessage(
							file.getLocalPath().getFileName() + " konnte nicht aktulaisiert werden. (" + e.getMessage() + ")"));
				}
			}
			if (executePath != null) {
				try {
					if (executePath.toString().toLowerCase().endsWith(".jar")) {
						ProcessBuilder builder = new ProcessBuilder("java", "-jar", executePath.toString());
						builder.start();
					} else {
						Desktop.getDesktop().open(executePath.toFile());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			try {
				Thread.sleep(5 * 1000L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Worker.shutdown();
			Platform.runLater(this::closeStage);
		});
	}

	@Override
	public void initStage(Stage stage) {
		stage.setResizable(false);
		stage.setWidth(600);
		stage.setHeight(110);
		stage.setTitle("Updater");
		stage.getIcons().add(new Image("icons/icon.png"));
	}

	private double size;

	@Override
	public void startDownload(double size) {
		this.size = size;
	}

	@Override
	public void updateProgress(double value, double readSize) {
		Platform.runLater(() ->
		{
			progressBar.setProgress(value);
			progressLabel.setText(NumberUtils.convertBytesToAppropriateFormat(readSize) + "B" + "/" + NumberUtils.convertBytesToAppropriateFormat(size) + "B");
		});
	}
}
