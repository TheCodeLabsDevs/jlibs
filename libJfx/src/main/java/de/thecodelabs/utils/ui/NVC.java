package de.thecodelabs.utils.ui;

import de.thecodelabs.utils.threading.Worker;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;

public class NVC implements Alertable {

	private String fxmlPath;
	private Parent fxmlView;
	private ResourceBundle bundle;

	private Optional<NVCStage> stageContainer;

	private Optional<NVC> parentNVC;
	private Optional<Scene> parent;

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
		parent = Optional.empty();

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
		loadFXML(path + filename, localization);
	}

	private void loadFXML(String path, ResourceBundle localization) {
		fxmlPath = path;

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

	public void init() {
	}

	/**
	 * Wird erst aufgerufen, wenn eine Stage erzeugt wird. Die Methode muss überschrieben werden, damit sie die Stage initalizieren kann.
	 *
	 * @param stage Stage
	 */
	public void initStage(Stage stage) {
	}

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
	public void showInfoMessage(String message) {
		showInfoMessage(message, null);
	}

	public void showErrorMessage(String message) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showErrorMessage(message));
			return;
		}

		Alert alert = Alerts.getInstance().createAlert(AlertType.ERROR, null, message);
		stageContainer.ifPresent(nvcStage -> alert.initOwner(nvcStage.getStage()));
		alert.initModality(Modality.WINDOW_MODAL);
		alert.showAndWait();
	}

	public void showInfoMessage(String message, String header) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showInfoMessage(message, header));
			return;
		}

		Alert alert = Alerts.getInstance().createAlert(AlertType.INFORMATION, header, message);
		alert.initModality(Modality.WINDOW_MODAL);
		stageContainer.ifPresent(nvcStage -> alert.initOwner(nvcStage.getStage()));
		alert.showAndWait();
	}

	// Action methods
	public final boolean showStage() {
		if (getStageContainer().isPresent()) {
			getStageContainer().get().show();
			return true;
		} else {
			return false;
		}
	}

	public final boolean closeStage() {
		if (getStageContainer().isPresent()) {
			getStageContainer().get().close();
			return true;
		} else {
			return false;
		}
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
		stageContainer.ifPresent(sc -> {
			final ObservableMap<KeyCombination, Runnable> accelerators = sc.getStage().getScene().getAccelerators();
			final Runnable closeAction = () -> Platform.runLater(onClose);

			accelerators.put(new KeyCodeCombination(KeyCode.ESCAPE), closeAction);
			accelerators.put(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN), closeAction);
		});
	}

	public Window getContainingWindow() {
		return fxmlView.getScene().getWindow();
	}

	// Modify stage content
	public void reloadFxml(ResourceBundle resourceBundle) {
		loadFXML(fxmlPath, resourceBundle);
		getStageContainer().ifPresent(nvcStage -> {
			applyViewControllerToStage(nvcStage.getStage());
		});
	}

	public void fadeTo(NVC nvc, Duration duration) {
		NVCStage stage = getStageContainer().orElse(null);
		if (stage != null) {

			FadeTransition transition = new FadeTransition(duration, getParent());
			transition.setToValue(0);
			transition.setOnFinished((event) -> {
				nvc.parent = Optional.of(stage.getStage().getScene());
				nvc.parentNVC = Optional.of(this);
				nvc.getParent().setOpacity(0);
				nvc.applyViewControllerToStage(stage.getStage());
				FadeTransition fadeIn = new FadeTransition(duration, nvc.getParent());
				fadeIn.setFromValue(0);
				fadeIn.setToValue(1);
				fadeIn.playFromStart();
			});
			transition.playFromStart();
		} else {
			throw new IllegalStateException("NVC not contains NVCStage");
		}
	}

	public void fadeBack(Duration duration) {
		NVCStage stage = getStageContainer().orElse(null);
		Scene parent = this.parent.orElse(null);
		if (stage != null && parent != null) {

			FadeTransition transition = new FadeTransition(duration, getParent());
			transition.setToValue(0);
			transition.setOnFinished((event) -> {
				parent.getRoot().setOpacity(0);
				parentNVC.ifPresent(nvc -> nvc.initStage(stage.getStage()));
				stage.getStage().setScene(parent);

				FadeTransition fadeIn = new FadeTransition(duration, parent.getRoot());
				fadeIn.setFromValue(0);
				fadeIn.setToValue(1);
				fadeIn.playFromStart();
			});
			transition.playFromStart();
		} else {
			throw new IllegalStateException("NVC not contains NVCStage");
		}
	}
}
