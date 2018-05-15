package de.tobias.utils.nui;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import de.tobias.utils.ui.Alertable;
import de.tobias.utils.util.Worker;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class NVC implements Alertable {

	private Parent fxmlView;
	private ResourceBundle bundle;

	private Optional<NVCStage> stageContainer;

	public NVC load(String path, String filename) {
		return load(path, filename, (ResourceBundle) null);
	}

	public NVC load(String path, String filename, ResourceBundle bundle) {
		stageContainer = Optional.empty();
		loadFXML(path, filename, bundle);
		return this;
	}

	/**
	 * Lädt die FXML async
	 * 
	 * @param path
	 * @param filename
	 * @param onFinish
	 */
	public NVC load(String path, String filename, Consumer<NVC> onFinish) {
		return load(path, filename, null, onFinish);
	}

	/**
	 * Lädt die FXML async
	 * 
	 * @param path
	 * @param filename
	 * @param onFinish
	 */
	public NVC load(String path, String filename, ResourceBundle bundle, Consumer<NVC> onFinish) {
		stageContainer = Optional.empty();
		Task<Void> task = new Task<Void>() {

			@Override
			protected Void call() throws Exception {
				try {
					loadFXML(path, filename, bundle);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}
		};
		task.setOnSucceeded(e -> onFinish.accept(this));
		Worker.runLater(task);
		return this;
	}

	private void loadFXML(String path, String filename, ResourceBundle localization) {

		if (!filename.endsWith(".fxml")) {
			filename += ".fxml";
		}

		if (!path.endsWith("/")) {
			path += "/";
		}

		String fxmlPath = path + filename;

		// FXML erzeugen
		FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlPath));

		// MVC - Controller setzten (ViewController ist von jedem Controller die
		// Superklasse)
		loader.setController(this);
		if (localization != null) {
			loader.setResources(localization);
			bundle = localization;
		}

		try {
			loader.load();

			// MVC - Parent mit dem View
			fxmlView = loader.getRoot();
			fxmlView.setUserData(this);

			init();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void init() {}

	/**
	 * Wird erst aufgerufen, wenn eine Stage erzeugt wird. Die Methode muss überschrieben werden, damit sie die Stage initalizieren kann.
	 * 
	 * @param stage
	 *            Stage
	 */
	public void initStage(Stage stage) {}

	/**
	 * Fügt den SceneGrapf zu einer Stage hinzu
	 * 
	 * @return
	 */
	public NVCStage applyViewControllerToStage() {
		return applyViewControllerToStage(new Stage());
	}

	public NVCStage applyViewControllerToStage(Stage stage) {
		NVCStage nvcStage = new NVCStage(this, stage);
		stageContainer = Optional.of(nvcStage);
		return nvcStage;
	}

	public Parent getParent() {
		return fxmlView;
	}

	public Optional<NVCStage> getStageContainer() {
		return stageContainer;
	}

	public ResourceBundle getBundle() {
		return bundle;
	}

	// alertable
	public void showErrorMessage(String message) {
		showErrorMessage(message, Optional.empty());
	}

	public void showInfoMessage(String message) {
		showInfoMessage(message, null);
	}

	public void showErrorMessage(String message, Image icon) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showErrorMessage(message, icon));
			return;
		}

		Alert alert = new Alert(AlertType.ERROR);
		if (stageContainer.isPresent())
			alert.initOwner(stageContainer.get().getStage());
		alert.initModality(Modality.WINDOW_MODAL);
		alert.setContentText(message);
		if (icon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);
		}
		alert.showAndWait();
	}

	public void showErrorMessage(String message, Optional<Image> icon) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showErrorMessage(message, icon));
			return;
		}

		Alert alert = new Alert(AlertType.ERROR);
		if (stageContainer.isPresent())
			alert.initOwner(stageContainer.get().getStage());
		alert.initModality(Modality.WINDOW_MODAL);
		alert.setContentText(message);
		if (icon.isPresent()) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon.get());
		}
		alert.showAndWait();
	}

	public void showInfoMessage(String message, Image icon) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showInfoMessage(message, icon));
			return;
		}

		Alert alert = new Alert(AlertType.INFORMATION);
		if (stageContainer.isPresent())
			alert.initOwner(stageContainer.get().getStage());
		alert.initModality(Modality.WINDOW_MODAL);
		alert.setContentText(message);
		if (icon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);
		}
		alert.showAndWait();
	}

	public void showInfoMessage(String message, String header, Image icon) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showInfoMessage(message, header, icon));
			return;
		}

		Alert alert = new Alert(AlertType.INFORMATION);
		if (stageContainer.isPresent())
			alert.initOwner(stageContainer.get().getStage());
		alert.initModality(Modality.WINDOW_MODAL);
		alert.setContentText(message);
		alert.setHeaderText(header);
		if (icon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);
		}
		alert.showAndWait();
	}

	// Utils
	public static void setAnchor(Node node, double left, double top, double right, double bottom) {
		AnchorPane.setLeftAnchor(node, left);
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
	}

	public void addKeyListener(EventHandler<KeyEvent> listener) {
		stageContainer.ifPresent(sc -> sc.getStage().getScene().addEventHandler(KeyEvent.ANY, listener));
	}

	public void addCloseKeyShortcut(Runnable onClose) {
		stageContainer.ifPresent(sc -> sc.getStage().getScene().getAccelerators()
				.put(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN), () -> Platform.runLater(onClose)));
	}

	public Window getContainingWindow() {
		return fxmlView.getScene().getWindow();
	}
}