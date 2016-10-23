package de.tobias.utils.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * Diese Klasse verwaltet die Verbindung zwischen Model und View. Es ist möglich ein Fenster einfach zu erstellen. Nötig dafür ist eine FXML
 * Datei. Diese wird in ein Fenster gepackt.
 *
 * @version 1.0
 */
@Deprecated
public abstract class ContentViewController implements Refreshable, Alertable {

	/**
	 * Der geladene Inhalt der FXML Datei.
	 */
	private Parent fxmlView;

	private ResourceBundle bundle;

	private List<ContentViewController> subViewController = new ArrayList<>();

	public ContentViewController(String path, String root) {
		this(path, root, null);
	}

	/**
	 * Erstellt mit der gegebenen Stage ein aus der FXML Datei geladenes Fenster mit Controller.
	 *
	 * @param name
	 *            FXML Dateiname (kann ohne .fxml).
	 * @param path
	 *            Ordner der Datei.
	 * @param stage
	 *            Fenster in dem die FXML Datei eingebettet werden soll.
	 * @param icon
	 *            Fenster icon.
	 * @param localization
	 *            Stringdatei
	 */
	public ContentViewController(String name, String path, ResourceBundle localization) {
		if (!name.endsWith(".fxml")) {
			name += ".fxml";
		}

		String fxmlPath = path + name;

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

	/**
	 * Initalisieren der Viewcomponenten. Wird nach dem erfolgreichen Laden des Views aufgerufen.
	 */
	public void init() {}

	/**
	 * Setzt den CSS Style der Scene.
	 * 
	 * @param file
	 *            CSS Dateiname
	 * @param root
	 *            CSS Ordner
	 */
	public void setCSS(String file, String root) {
		if (!file.endsWith(".css")) {
			file += ".css";
		}
		fxmlView.getStylesheets().add(root + file);
	}

	/**
	 * Das im FXML-Code definierte Objekt, wird hier als Parent gegeben.
	 * 
	 * @return FXML als Parent
	 */
	public Parent getParent() {
		return fxmlView;
	}

	public void addSubViewController(ContentViewController controller) {
		subViewController.add(controller);
	}

	public List<ContentViewController> getSubViewController() {
		return subViewController;
	}

	/**
	 * ResoucreBundler des ViewController.
	 * 
	 * @return resourceBundler
	 */
	public ResourceBundle getResourceBundler() {
		return bundle;
	}

	public Window getWindow() {
		return fxmlView.getScene().getWindow();
	}

	@Override
	public void updateData() {}

	protected void loadSettings(BasicControllerSettings settings) {}

	protected void save(BasicControllerSettings settings) {}

	/*
	 * http://code.makery.ch/blog/javafx-dialogs-official/
	 */
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
		alert.initOwner(getWindow());
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
		alert.initOwner(getWindow());
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
		alert.initOwner(getWindow());
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
		alert.initOwner(getWindow());
		alert.initModality(Modality.WINDOW_MODAL);
		alert.setContentText(message);
		alert.setHeaderText(header);
		if (icon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);
		}
		alert.showAndWait();
	}

	public Window getStage() {
		return getWindow();
	}

	public static void setAnchor(Node node, double left, double top, double right, double bottom) {
		AnchorPane.setLeftAnchor(node, left);
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
	}
}
