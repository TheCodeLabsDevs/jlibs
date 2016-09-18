package de.tobias.utils.ui;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import de.tobias.utils.application.ApplicationUtils;
import de.tobias.utils.application.container.PathType;
import de.tobias.utils.settings.YAMLSettings;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Diese Klasse verwaltet die Verbindung zwischen Model und View. Es ist möglich ein Fenster einfach zu erstellen. Nötig dafür ist eine FXML
 * Datei. Diese wird in ein Fenster gepackt.
 *
 * @version 1.0
 */
public abstract class ViewController implements Refreshable, Alertable {

	/**
	 * Create a new ViewController. The ViewController must contain a constructor with a Stage Parameter.
	 * 
	 * @param clazz
	 * @param stage
	 * @return New instance of ViewController
	 * @throws Exception
	 */
	public static <T> T create(Class<T> clazz, Stage stage) throws Exception {
		return clazz.getConstructor(Stage.class).newInstance(stage);
	}

	/**
	 * Create a new ViewController.
	 * 
	 * @param clazz
	 * @return New instance of ViewController
	 * @throws Exception
	 */
	public static <T> T create(Class<T> clazz) throws Exception {
		return clazz.getConstructor().newInstance();
	}

	/**
	 * Fenster, in der die FXML Datei gepackt wird.
	 * 
	 * @see ViewController#fxmlView
	 */
	private Stage stage;

	/**
	 * Der geladene Inhalt der FXML Datei.
	 */
	private Parent fxmlView;

	/**
	 * Path zu den Einstellungen
	 */
	private Path configPath;

	/**
	 * Speichert das Aussehen des Fensters
	 */
	private BasicControllerSettings settings;

	private List<Runnable> closeHook;

	private ResourceBundle bundle;

	private List<ContentViewController> subViewController = new ArrayList<>();

	/**
	 * Erstellt ein neues Fenster im Controller aus der FXML Datei.
	 *
	 * @param path
	 *            FXML Dateiname (kann ohne .fxml).
	 * @param root
	 *            Ordner der Datei.
	 * @param icon
	 *            Fenster icon.
	 * @param localization
	 *            Stringdatei
	 */
	public ViewController(String path, String root, Image icon, ResourceBundle localization) {
		this(path, root, new Stage(), icon, localization);
	}

	public ViewController(String path, String root) {
		this(path, root, null, null);
	}

	public ViewController(String path, String root, Stage stage, Image icon, ResourceBundle localization) {
		this(path, root, stage, icon, localization, BasicControllerSettings.class);
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
	public ViewController(String name, String path, Stage stage, Image icon, ResourceBundle localization,
			Class<? extends BasicControllerSettings> settingsClass) {
		this.stage = stage;

		if (!name.endsWith(".fxml")) {
			name += ".fxml";
		}

		if (!path.endsWith("/")) {
			path += "/";
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

		this.closeHook = new ArrayList<>();

		try {
			loader.load();

			// MVC - Parent mit dem View
			fxmlView = loader.getRoot();
			fxmlView.setUserData(this);
			Scene scene = new Scene(fxmlView);

			stage.setScene(scene);
			stage.centerOnScreen();

			// Fenster Einstellungen
			if (icon != null)
				Platform.runLater(() -> stage.getIcons().add(icon));

			// Einstellungen speichern
			stage.setOnHidden((event) ->
			{
				saveSettings();
			});

			stage.setOnCloseRequest((event) ->
			{
				boolean close = closeRequest();
				if (!close) {
					event.consume();
				}
				closeHook.forEach(Runnable::run);
			});

			// Einstellungen laden
			try {
				fxmlPath = fxmlPath.replace(File.separator, ".").concat(".yml");
				this.configPath = ApplicationUtils.getApplication().getPath(PathType.CONFIGURATION, fxmlPath);
				this.settings = YAMLSettings.load(settingsClass, this.configPath);
			} catch (Exception e1) {
				e1.printStackTrace();
			}

			init();
			loadSettings(settings);
			initStage(stage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public BasicControllerSettings getSettings() {
		return settings;
	}

	/**
	 * Lädt die Eigenschaften zum View. Für Benutzerdefinierte Eigenschaften ist @see BasicControllerSettings#userInfo.
	 * 
	 * @param settings
	 *            Eigenschaften
	 */
	protected void loadSettings(BasicControllerSettings settings) {
		stage.setIconified(settings.iconified);
		stage.setWidth(settings.width);
		stage.setHeight(settings.height);

		subViewController.forEach(item -> item.loadSettings(settings));
	}

	/**
	 * Speichert die Eigenschaften des View. Für Benutzerdefinierte Eigenschaften ist @see BasicControllerSettings#userInfo.
	 * 
	 * @param settings
	 *            Eigenschaften
	 */
	protected void save(BasicControllerSettings settings) {
		settings.iconified = stage.isIconified();
		settings.width = stage.getWidth();
		settings.height = stage.getHeight();

		subViewController.forEach(item -> item.save(settings));
	}

	public final void saveSettings() {
		try {
			save(settings);
			YAMLSettings.save(settings, ViewController.this.configPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addSubViewController(ContentViewController controller) {
		subViewController.add(controller);
	}

	/**
	 * Initalisieren der Viewcomponenten. Wird nach dem erfolgreichen Laden des Views aufgerufen.
	 */
	public void init() {}

	/**
	 * Initalisiere dein Fenster, nachdem alles geladen ist.
	 *
	 * @param stage
	 *            Fenster, welches zum Controller gehört.
	 */
	public void initStage(Stage stage) {}

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
		stage.getScene().getStylesheets().add(root + file);
	}

	/**
	 * Fenster, welches zum Controller gehört.
	 * 
	 * @return Stage
	 */
	public Stage getStage() {
		return stage;
	}

	/**
	 * Das im FXML-Code definierte Objekt, wird hier als Parent gegeben.
	 * 
	 * @return FXML als Parent
	 */
	public Parent getParent() {
		return fxmlView;
	}

	/**
	 * Fügt einen hook hinzu, wenn die View geschlossen wird.
	 * 
	 * @param runnable
	 */
	public void addCloseHook(Runnable runnable) {
		this.closeHook.add(runnable);
	}

	/**
	 * ResoucreBundler des ViewController.
	 * 
	 * @return resourceBundler
	 */
	public ResourceBundle getResourceBundle() {
		return bundle;
	}

	public Screen getScreen() {
		List<Screen> screens = Screen.getScreensForRectangle(getStage().getX(), getStage().getY(), getStage().getWidth(),
				getStage().getHeight());
		return screens.get(0);
	}

	@Override
	public void updateData() {}

	public boolean closeRequest() {
		return true;
	}

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
		alert.initOwner(stage);
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
		alert.initOwner(stage);
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
		alert.initOwner(stage);
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
		alert.initOwner(stage);
		alert.initModality(Modality.WINDOW_MODAL);
		alert.setContentText(message);
		alert.setHeaderText(header);
		if (icon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);
		}
		alert.showAndWait();
	}

	public static void setAnchor(Node node, double left, double top, double right, double bottom) {
		AnchorPane.setLeftAnchor(node, left);
		AnchorPane.setTopAnchor(node, top);
		AnchorPane.setRightAnchor(node, right);
		AnchorPane.setBottomAnchor(node, bottom);
	}

	public void addKeyListener(EventHandler<KeyEvent> listener) {
		getStage().getScene().addEventHandler(KeyEvent.ANY, listener);
	}

	public void addCloseKeyShortcut(Runnable onClose) {
		getStage().getScene().getAccelerators().put(new KeyCodeCombination(KeyCode.W, KeyCombination.SHORTCUT_DOWN),
				() -> Platform.runLater(onClose));
	}
}
