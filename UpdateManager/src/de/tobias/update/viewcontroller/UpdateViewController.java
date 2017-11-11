package de.tobias.update.viewcontroller;

import java.io.IOException;
import java.nio.file.Files;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.Stage;
import de.tobias.update.main.Update;
import de.tobias.update.main.Update.Status;
import de.tobias.update.view.UpdateCell;
import de.tobias.utils.application.container.FileContainer;
import de.tobias.utils.ui.ViewController;
import de.tobias.utils.util.SystemUtils;
import de.tobias.utils.util.Worker;

public class UpdateViewController extends ViewController {

	@FXML private ListView<Update> listView;
	@FXML private Button startButton;
	@FXML private ProgressBar progressBar;

	public UpdateViewController(Stage stage, String appKey) {
		super("updateView.fxml", "de/tobias/update/assets/", stage, null, null);

		if (appKey == null) {
			loadUpdates();
		} else {
			loadUpdate(appKey);
		}
	}

	@Override
	public void init() {
		listView.setCellFactory(view -> new UpdateCell());
	}

	@Override
	public void initStage(Stage stage) {
		stage.setTitle("Update Manager");
		stage.setMinWidth(600);
		stage.setMinHeight(400);
		stage.setOnCloseRequest(event -> Worker.shutdown());
	}

	private void loadUpdates() {
		try {
			Files.newDirectoryStream(SystemUtils.getApplicationSupportDirectoryPath(FileContainer.containerName)).forEach(path -> {
				String fileName = path.getFileName().toString();
				if (!fileName.endsWith(".debug") && Files.isDirectory(path)) {
					loadUpdate(fileName);
				}
			});
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	private void loadUpdate(String key) {
		Worker.runLater(() -> {
			try {
				Update update = Update.load(key);
				if (update != null) {
					listView.getItems().add(update);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	@FXML
	private void startHandler(ActionEvent event) {
		Worker.runLater(() -> {
			listView.getItems().forEach(item -> {
				try {
					if (item.getUpdateEnable()) {
						Platform.runLater(() -> item.setStatus(Status.BEGIN));
						item.getApp().getContainer().updateApp(progress -> Platform.runLater(() -> progressBar.setProgress(progress)));
						Platform.runLater(() -> item.setStatus(Status.FINISH));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
		});
	}

}
