package de.thecodelabs.utils.ui.notification;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class NotificationContent extends AnchorPane
{
	private static final double SPACING = 15.0;

	private double width;
	private double height;
	private double padding;
	private Image icon;
	private double iconSize;
	private String title;
	private String description;

	private Button closeButton;

	public NotificationContent(double width, double height, double padding, Image icon, double iconSize, String title, String description)
	{
		this.width = width;
		this.height = height;
		this.padding = padding;
		this.icon = icon;
		this.iconSize = iconSize;
		this.title = title;
		this.description = description;

		init();
	}

	private void init()
	{
		setPrefWidth(width - 2 * padding);
		setPrefHeight(height - 2 * padding);
		getStyleClass().add("notification-background");

		closeButton = new Button();
		closeButton.getStyleClass().add("notification-button");
		closeButton.setFocusTraversable(false);

		final StackPane graphic = new StackPane();
		graphic.getStyleClass().setAll("graphic");
		closeButton.setGraphic(graphic);

		getChildren().add(closeButton);
		AnchorPane.setRightAnchor(closeButton, 5.0);
		AnchorPane.setTopAnchor(closeButton, 0.0);

		final ImageView view = new ImageView(icon);
		view.setFitWidth(iconSize);
		view.setFitHeight(iconSize);

		getChildren().add(view);
		AnchorPane.setLeftAnchor(view, SPACING);
		AnchorPane.setTopAnchor(view, (height - padding - iconSize) / 2.0);

		final Label labelTitle = new Label(title);
		labelTitle.getStyleClass().add("notification-label-title");

		getChildren().add(labelTitle);
		AnchorPane.setLeftAnchor(labelTitle, SPACING + iconSize + 25.0);
		AnchorPane.setTopAnchor(labelTitle, SPACING);

		final Label labelDescription = new Label(description);
		labelDescription.getStyleClass().add("notification-label-description");
		labelDescription.setPrefWidth(width - padding - iconSize - 35.0);
		labelDescription.setWrapText(true);

		getChildren().add(labelDescription);
		AnchorPane.setLeftAnchor(labelDescription, SPACING + iconSize + 25.0);
		AnchorPane.setTopAnchor(labelDescription, 50.0);
	}

	public Button getCloseButton()
	{
		return closeButton;
	}
}
