package de.tobias.utils.nui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public final class NVCStage {

	public interface CloseHook {

		/**
		 * Wird beim Schließen des Fensters aufgerufen.
		 * 
		 * @return <code>true</code> schließen
		 */
		public boolean onClose();
	}

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(NVCDatabase::save));
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
		NVCItem loadItem = NVCDatabase.getItem(viewController.getClass());

		ObservableList<Screen> screens = Screen.getScreensForRectangle(loadItem.getPosX(), loadItem.getPosY(), loadItem.getWidth(),
				loadItem.getHeight());
		if (screens.size() != 0) {
			stage.setX(loadItem.getPosX());
			stage.setY(loadItem.getPosY());
			stage.setWidth(loadItem.getWidth());
			stage.setHeight(loadItem.getHeight());
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
	 * @param image
	 *            Image
	 * 
	 * @throws IllegalStateException
	 *             Stage is null
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
	 * @param path
	 *            Path zu stylesheet
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
