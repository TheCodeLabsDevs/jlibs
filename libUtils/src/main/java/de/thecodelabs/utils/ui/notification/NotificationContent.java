package de.thecodelabs.utils.ui.notification;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class NotificationContent extends AnchorPane
{
	private double width;
	private double height;
	private double offset;
	private Image icon;
	private double iconSize;
	private String title;
	private String description;

	private Button closeButton;

	public NotificationContent(double width, double height, double offset, Image icon, double iconSize, String title, String description)
	{
		this.width = width;
		this.height = height;
		this.offset = offset;
		this.icon = icon;
		this.iconSize = iconSize;
		this.title = title;
		this.description = description;

		init();
	}

	private void init()
	{
		setPrefWidth(width - 10);
		setPrefHeight(height - 10);
		getStyleClass().add("notification-background");

		closeButton = new Button();
		closeButton.getStyleClass().add("notification-button");
		closeButton.setFocusTraversable(false);

		StackPane graphic = new StackPane();
		graphic.getStyleClass().setAll("graphic");
		closeButton.setGraphic(graphic);

		getChildren().add(closeButton);
		AnchorPane.setRightAnchor(closeButton, 5.0);
		AnchorPane.setTopAnchor(closeButton, 0.0);

		ImageView view = new ImageView(icon);
		view.setFitWidth(iconSize);
		view.setFitHeight(iconSize);

		getChildren().add(view);
		AnchorPane.setLeftAnchor(view, 15.0);
		AnchorPane.setTopAnchor(view, (height - offset - iconSize) / 2.0);

		Label labelTitle = new Label(title);
		labelTitle.getStyleClass().add("notification-label-title");

		getChildren().add(labelTitle);
		AnchorPane.setLeftAnchor(labelTitle, 15.0 + iconSize + 25.0);
		AnchorPane.setTopAnchor(labelTitle, 15.0);

		Label labelDescription = new Label(description);
		labelDescription.getStyleClass().add("notification-label-description");
		labelDescription.setPrefWidth(width - offset - iconSize - 35.0);
		labelDescription.setWrapText(true);

		getChildren().add(labelDescription);
		AnchorPane.setLeftAnchor(labelDescription, 15.0 + iconSize + 25.0);
		AnchorPane.setTopAnchor(labelDescription, 50.0);
	}

	public Button getCloseButton()
	{
		return closeButton;
	}
}
