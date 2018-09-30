package de.tobias.utils.ui;

import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Alerts {

	public static Alert createAlert(Alert.AlertType alertType, String title, String message, Image icon) {
		Alert alert = new Alert(alertType);
		if (title != null) {
			alert.setHeaderText(title);
		}
		alert.setContentText(message);
		if (icon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(icon);
		}

		return alert;
	}
}
