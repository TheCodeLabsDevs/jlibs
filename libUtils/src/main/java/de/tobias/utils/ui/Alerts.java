package de.tobias.utils.ui;

import de.tobias.utils.util.ColorUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Alerts {

	private static Alerts sharedInstance;

	public static Alerts getInstance() {
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

	public Alert createAlert(AlertType alertType, String title, String message) {
		Alert alert = new Alert(alertType);
		if (title != null) {
			alert.setTitle(title);
		}

		if (defaultHeaderText != null) {
			alert.setHeaderText(defaultHeaderText);
		}

		if (defaultBaseColor != null) {
			alert.getDialogPane().setStyle("-fx-base: " + ColorUtils.toRGBHexWithoutOpacity(defaultBaseColor));
		}

		alert.setContentText(message);
		alert.initModality(Modality.WINDOW_MODAL);
		if (defaultIcon != null) {
			Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			stage.getIcons().add(defaultIcon);
			stage.centerOnScreen();
		}

		return alert;
	}

	public Alert createAlert(AlertType alertType, String title, String message, Window owner) {
		Alert alert = createAlert(alertType, title, message);
		alert.initOwner(owner);
		return alert;
	}

	public Alert createAlert(AlertType alertType, String title, String headerText, String message, Window owner) {
		Alert alert = createAlert(alertType, title, message);
		alert.setHeaderText(headerText);
		alert.initOwner(owner);
		return alert;
	}
}
