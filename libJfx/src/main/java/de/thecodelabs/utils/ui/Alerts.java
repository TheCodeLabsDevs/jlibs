package de.thecodelabs.utils.ui;

import de.thecodelabs.utils.jfx.ColorUtils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.util.List;

public class Alerts
{

	private static Alerts sharedInstance;

	public static Alerts getInstance()
	{
		if(sharedInstance == null)
		{
			sharedInstance = new Alerts();
		}
		return sharedInstance;
	}

	private Image defaultIcon;
	private String defaultHeaderText;
	private Color defaultBaseColor;
	private List<String> defaultStyleSheets;

	public Image getDefaultIcon()
	{
		return defaultIcon;
	}

	public void setDefaultIcon(Image defaultIcon)
	{
		this.defaultIcon = defaultIcon;
	}

	public String getDefaultHeaderText()
	{
		return defaultHeaderText;
	}

	public void setDefaultHeaderText(String defaultHeaderText)
	{
		this.defaultHeaderText = defaultHeaderText;
	}

	public Color getDefaultBaseColor()
	{
		return defaultBaseColor;
	}

	public void setDefaultBaseColor(Color defaultBaseColor)
	{
		this.defaultBaseColor = defaultBaseColor;
	}

	public List<String> getDefaultStyleSheets()
	{
		return defaultStyleSheets;
	}

	public void setDefaultStyleSheets(List<String> defaultStyleSheets)
	{
		this.defaultStyleSheets = defaultStyleSheets;
	}

	public Alert createAlert(AlertType alertType, String title, String message)
	{
		Alert alert = new Alert(alertType);
		if(title != null)
		{
			alert.setTitle(title);
		}

		if(defaultHeaderText != null)
		{
			alert.setHeaderText(defaultHeaderText);
		}

		if(defaultBaseColor != null)
		{
			alert.getDialogPane().setStyle("-fx-base: " + ColorUtils.toRGBHexWithoutOpacity(defaultBaseColor));
		}

		alert.setContentText(message);
		alert.initModality(Modality.WINDOW_MODAL);

		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.centerOnScreen();

		if(defaultIcon != null)
		{
			stage.getIcons().add(defaultIcon);
		}
		if(defaultStyleSheets != null)
		{
			DialogPane dialogPane = alert.getDialogPane();
			dialogPane.getStylesheets().addAll(defaultStyleSheets);
		}
		return alert;
	}

	public Alert createAlert(AlertType alertType, String title, String message, Window owner)
	{
		Alert alert = createAlert(alertType, title, message);
		alert.initOwner(owner);
		return alert;
	}

	public Alert createAlert(AlertType alertType, String title, String headerText, String message, Window owner)
	{
		Alert alert = createAlert(alertType, title, message);
		alert.setHeaderText(headerText);
		alert.initOwner(owner);
		return alert;
	}
}
