package de.tobias.utils.ui;

import de.tobias.utils.util.ColorUtils;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Alerts {

	private static Alerts sharedInstance;

	public static Alerts shared() {
		if (sharedInstance == null) {
			sharedInstance = new Alerts();
		}
		return sharedInstance;
	}

	private Image defaultIcon;
	private String defaultHeaderText;
	private Color defaultBaseColor;

	public Image getDefaultIcon() {
		return defaultIcon;
	}

	public void setDefaultIcon(Image defaultIcon) {
		this.defaultIcon = defaultIcon;
	}

	public String getDefaultHeaderText() {
		return defaultHeaderText;
	}

	public void setDefaultHeaderText(String defaultHeaderText) {
		this.defaultHeaderText = defaultHeaderText;
	}

	public Color getDefaultBaseColor() {
		return defaultBaseColor;
	}

	public void setDefaultBaseColor(Color defaultBaseColor) {
		this.defaultBaseColor = defaultBaseColor;
	}

	public Alert createAlert(Alert.AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		if (title != null) {
			alert.setHeaderText(title);
		}

		if (defaultHeaderText != null) {
			alert.setHeaderText(defaultHeaderText);
		}

		if (defaultBaseColor != null) {
			alert.getDialogPane().setStyle("-fx-base: " + ColorUtils.toRGBHex(defaultBaseColor));
		}

		alert.setContentText(message);
		if (defaultIcon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(defaultIcon);
			stage.centerOnScreen();
		}

		return alert;
	}

	public Alert createAlert(Alert.AlertType alertType, String title, String message, Window owner) {
		Alert alert = createAlert(alertType, title, message);
		alert.initOwner(owner);
		return alert;
	}

	public Alert createAlert(Alert.AlertType alertType, String title, String headerText, String message, Window owner) {
		Alert alert = createAlert(alertType, title, message);
		alert.setHeaderText(headerText);
		alert.initOwner(owner);
		return alert;
	}
}
