package de.tobias.utils.nui;

import de.tobias.utils.nui.size.IgnoreStageSizing;
import de.tobias.utils.nui.size.NVCDatabase;
import de.tobias.utils.nui.size.NVCItem;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class NVCStage {

	public interface CloseHook {

		/**
		 * Wird beim Schließen des Fensters aufgerufen.
		 *
		 * @return <code>true</code> schließen
		 */
		boolean onClose();
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(NVCDatabase::save));
	}

	private static boolean disabledSizeLoading;

	public static boolean isDisabledSizeLoading() {
		return disabledSizeLoading;
	}

	public static void setDisabledSizeLoading(boolean disabledSizeLoading) {
		NVCStage.disabledSizeLoading = disabledSizeLoading;
	}

	private NVC viewController;
	private Stage stage;
	private List<CloseHook> closeHook;

	NVCStage(NVC viewController, Stage stage) {
		this.viewController = viewController;
		this.stage = stage;
		this.closeHook = new ArrayList<>();

		init();
	}

	public NVCStage initOwner(Window owner) {
		stage.initOwner(owner);
		return this;
	}

	public NVCStage initModality(Modality modality) {
		stage.initModality(modality);
		return this;
	}

	private void init() {
		// First Load States
		NVCDatabase.load();

		Scene scene = new Scene(viewController.getParent());

		stage.setScene(scene);

		// Load Settings
		if (!viewController.getClass().isAnnotationPresent(IgnoreStageSizing.class) && !isDisabledSizeLoading()) {
			NVCItem item = NVCDatabase.getItem(viewController.getClass());

			ObservableList<Screen> screens = Screen.getScreensForRectangle(item.getPosX(), item.getPosY(), item.getWidth(), item.getHeight());
			if (screens.size() != 0) {
				if (!Double.isNaN(item.getPosX()) && !Double.isNaN(item.getPosY())) {
					stage.setX(item.getPosX());
					stage.setY(item.getPosY());
				} else {
					stage.centerOnScreen();
				}

				stage.setWidth(item.getWidth());
				stage.setHeight(item.getHeight());
			}
		} else {
			System.out.println("Skip stage sizing");
		}

		// Init Close Handlers
		stage.setOnCloseRequest(e ->
		{
			// Event Handlers
			for (CloseHook hook : closeHook) {
				if (!hook.onClose()) {
					e.consume();
				}
			}
		});

		stage.setOnHiding(e ->
		{
			// Svae Settings
			NVCItem saveItem = NVCDatabase.getItem(viewController.getClass());
			saveItem.setPosX(stage.getX());
			saveItem.setPosY(stage.getY());
			saveItem.setWidth(stage.getWidth());
			saveItem.setHeight(stage.getHeight());
		});

		viewController.initStage(stage);
	}

	/**
	 * Setzt das Icon der Stage.
	 *
	 * @param image Image
	 * @throws IllegalStateException Stage is null
	 */
	public void setImage(Image image) {
		if (stage == null) {
			throw new IllegalStateException("Stage is null");
		}
		stage.getIcons().add(image);
	}

	public void setImage(Optional<Image> image) {
		image.ifPresent(this::setImage);
	}

	/**
	 * Fügt ein Stylesheet hinzu.
	 *
	 * @param path Path zu stylesheet
	 */
	public void addStylesheet(String path) {
		if (stage == null) {
			throw new IllegalStateException("Stage is null");
		}

		if (!stage.getScene().getStylesheets().contains(path)) {
			stage.getScene().getStylesheets().add(path);
		}
	}

	public void show() {
		stage.show();
	}

	public void showAndWait() {
		stage.showAndWait();
	}

	public void close() {
		stage.close();
	}

	public Stage getStage() {
		return stage;
	}

	public Screen getScreen() {
		List<Screen> screens = Screen.getScreensForRectangle(getStage().getX(), getStage().getY(), getStage().getWidth(),
				getStage().getHeight());
		return screens.get(0);
	}

	public void addCloseHook(CloseHook hook) {
		closeHook.add(hook);
	}
}
