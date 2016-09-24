package de.tobias.updater.viewcontroller;

import java.awt.Desktop;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import de.tobias.updater.main.UpdateFile;
import de.tobias.utils.ui.ViewController;
import de.tobias.utils.util.NumberUtils;
import de.tobias.utils.util.Worker;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class UpdateViewController extends ViewController {

	@FXML private ProgressBar progressBar;
	@FXML private Label detailLabel;
	@FXML private Label progressLabel;
	private int done = 1;

	public UpdateViewController(Path downloadPath, List<UpdateFile> files, Path executePath) {
		super("updateView", "de/tobias/updater/assets");

		Worker.runLater(() ->
		{
			for (UpdateFile file : files) {
				try {
					Platform.runLater(() ->
					{
						detailLabel.setText("Update (" + done + "/" + files.size() + "): " + file.getLocal().getFileName());
						done++;
					});
					Path downloadFle = downloadFile(downloadPath, file);
					installFile(downloadFle, file);
				} catch (Exception e) {
					e.printStackTrace();
					Platform.runLater(() -> showErrorMessage(
							file.getLocal().getFileName() + " konnte nicht aktulaisiert werden. (" + e.getMessage() + ")"));
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
			Worker.shutdown();
			Platform.runLater(() -> getStage().close());
		});
	}

	@Override
	public void initStage(Stage stage) {
		stage.setResizable(false);
		stage.setWidth(600);
		stage.setHeight(110);
		stage.setTitle("Updater");
		stage.getIcons().add(new Image("de/tobias/updater/assets/icon.png"));
	}

	private int readSize = 0;

	private Path downloadFile(Path downloadFolder, UpdateFile file) throws IOException {
		readSize = 0;

		Path path = downloadFolder.resolve(file.getLocal().getFileName());
		if (Files.notExists(path)) {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}

		URLConnection conn = file.getUrl().openConnection();
		int size = Integer.valueOf(conn.getHeaderField("content-Length"));

		InputStream iStr = conn.getInputStream();
		OutputStream oStr = Files.newOutputStream(path);

		byte[] data = new byte[1024];
		int dataLength = 0;
		while ((dataLength = iStr.read(data, 0, data.length)) > 0) {
			oStr.write(data, 0, dataLength);
			readSize += dataLength;

			Platform.runLater(() ->
			{
				progressBar.setProgress(readSize / (double) size);
				progressLabel.setText(NumberUtils.numberToString(readSize) + "B" + "/" + NumberUtils.numberToString(size) + "B");
			});
		}
		oStr.close();

		return path;
	}

	private void installFile(Path downloadFile, UpdateFile file) throws IOException {
		Files.copy(downloadFile, file.getLocal(), StandardCopyOption.REPLACE_EXISTING);
		Files.deleteIfExists(downloadFile);
	}
}
