package de.tobias.update.view;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import de.tobias.update.main.Update;
import de.tobias.update.main.Update.Status;
import de.tobias.utils.application.ApplicationInfo;

public class UpdateCell extends ListCell<Update> {

	private CheckBox checkBox;
	private Image image;
	private ImageView imageView;
	private Label name;
	private Label versionOld;
	private Label versionNew;
	private Label status;

	private HBox rootBox;
	private HBox versionBox;
	private VBox infoBox;

	private Update representedItem;

	private ChangeListener<Status> listener;

	public UpdateCell() {
		checkBox = new CheckBox();
		checkBox.setSelected(true);

		imageView = new ImageView();
		name = new Label();
		versionOld = new Label();
		versionNew = new Label();
		status = new Label();

		rootBox = new HBox();
		infoBox = new VBox();
		versionBox = new HBox();

		
		imageView.setFitWidth(48);
		imageView.setFitHeight(48);

		versionBox.getChildren().addAll(new Label("Neue Version:"), versionNew);
		infoBox.getChildren().addAll(name, versionOld);
		rootBox.getChildren().addAll(checkBox, imageView, infoBox, versionBox, status);
		
		status.setAlignment(Pos.CENTER_RIGHT);
		status.setMaxWidth(Double.MAX_VALUE);
		HBox.setHgrow(status, Priority.ALWAYS);

		rootBox.setAlignment(Pos.CENTER_LEFT);
		rootBox.setSpacing(20);

		versionBox.setAlignment(Pos.CENTER_LEFT);
		versionBox.setSpacing(5);

		infoBox.setAlignment(Pos.CENTER_LEFT);
		infoBox.setSpacing(5);

		listener = (a, b, c) -> {
			if (c != null) {
				if (c == Status.NOTSTARTED) {
					status.setText("Ausstehend");
				} else if (c == Status.BEGIN) {
					status.setText("In Bearbeitung");
				} else if (c == Status.FINISH) {
					status.setText("Fertig");
				}
			}
		};
	}

	@Override
	protected void updateItem(Update update, boolean arg1) {
		super.updateItem(update, arg1);
		if (!arg1) {
			this.representedItem = update;

			ApplicationInfo info = update.getApp().getInfo();
			name.setText(info.getName());
			versionOld.setText(info.getVersion());
			versionNew.setText(update.getNewVersion());
			listener.changed(null, null, update.getStatus());

			if (info.getIconPath() != null) {
				try {
					image = new Image(update.loadFileFromArchive(info.getIconPath() + ".png"));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				imageView.setImage(image);
			} else {
				imageView.setImage(null);
			}

			this.representedItem.statusProterty().addListener(listener);
			this.representedItem.updateEnableProperty().bind(checkBox.selectedProperty());
			setGraphic(rootBox);
		} else {
			if (this.representedItem != null) {
				this.representedItem.updateEnableProperty().unbind();
				this.representedItem.statusProterty().removeListener(listener);
			}
			setGraphic(null);
		}
	}
}
