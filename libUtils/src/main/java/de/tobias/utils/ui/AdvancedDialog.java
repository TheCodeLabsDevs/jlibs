package de.tobias.utils.ui;

import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.Optional;

public class AdvancedDialog extends Dialog<ButtonType> {

	private CheckBox checkBox;
	private Label contentLabel;

	public AdvancedDialog(Window owner) {
		contentLabel = new Label();
		checkBox = new CheckBox();
		VBox vBox = new VBox(14, contentLabel, checkBox);

		getDialogPane().setContent(vBox);

		initOwner(owner);
		initModality(Modality.WINDOW_MODAL);
	}

	public void setContent(String text) {
		contentLabel.setText(text);
	}

	public void addButtonType(ButtonType button) {
		getDialogPane().getButtonTypes().add(button);
	}

	public void setIcon(Image image) {
		Stage dialogStage = (Stage) getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(image);
	}

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	public void setIcon(Optional<Image> image) {
		Stage dialogStage = (Stage) getDialogPane().getScene().getWindow();
		image.ifPresent(dialogStage.getIcons()::add);
	}

	public void setCheckboxText(String text) {
		checkBox.setText(text);
	}

	public boolean isSelected() {
		return checkBox.isSelected();
	}
}
