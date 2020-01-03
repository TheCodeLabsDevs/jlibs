package de.thecodelabs.utils.ui.notification;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class Notification {
	private int width;
	private int height;
	private int offset;
	private int iconSize;

	private String title;
	private String description;
	private Image icon;
	private Image defaultIcon;
	private int hideAfterInMillis;
	private final int DEFAULT_HIDE_AFTER_IN_MILLIS = 4000;
	private int fadeOutTimeInMillis;
	private final int DEFAULT_FADE_OUT_TIME_IN_MILLIS = 1000;
	private Timeline timeline;

	private Stage stage;
	private Stage owner;
	private String styleSheet;

	public Notification(int width, int height, int offset, int iconSize, int hideAfterInMillis, int fadeOutTimeInMillis, Stage owner, Image defaultIcon, String styleSheet) {
		this.width = width;
		this.height = height;
		this.offset = offset;
		this.iconSize = iconSize;
		this.hideAfterInMillis = hideAfterInMillis;
		this.fadeOutTimeInMillis = fadeOutTimeInMillis;
		this.owner = owner;
		this.defaultIcon = defaultIcon;
		if (styleSheet == null) {
			this.styleSheet = "notification/style/defaultNotificationStyle.css";
		} else {
			this.styleSheet = styleSheet;
		}
	}

	public void close() {
		if (stage != null) {
			stage.close();
			timeline.stop();
		}
	}

	public void show() {
		try {
			if (hideAfterInMillis == 0) {
				hideAfterInMillis = DEFAULT_HIDE_AFTER_IN_MILLIS;
			}

			if (fadeOutTimeInMillis == 0) {
				fadeOutTimeInMillis = DEFAULT_FADE_OUT_TIME_IN_MILLIS;
			}

			// screen size
			Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
			stage = new Stage();
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setX(primaryScreenBounds.getMaxX() - width - offset);
			stage.setY(primaryScreenBounds.getMaxY() - height - offset);
			stage.setAlwaysOnTop(true);

			AnchorPane root = new AnchorPane();
			root.setPrefWidth(width);
			root.setPrefHeight(height);
			root.setBackground(Background.EMPTY);
			root.getStylesheets().add(styleSheet);

			// notification content
			AnchorPane content = new AnchorPane();
			content.setPrefWidth(width - 10);
			content.setPrefHeight(height - 10);
			content.getStyleClass().add("notification-background");
			root.getChildren().add(content);
			AnchorPane.setLeftAnchor(content, 5.0);
			AnchorPane.setTopAnchor(content, 5.0);

			Button button = new Button("x");
			button.getStyleClass().add("notification-button");
			button.setFocusTraversable(false);
			button.setOnAction(event -> {
				stage.close();
				timeline.stop();
			});

			content.getChildren().add(button);
			AnchorPane.setRightAnchor(button, 5.0);
			AnchorPane.setTopAnchor(button, 0.0);

			// use default icon if icon is missing
			if (icon == null) {
				icon = defaultIcon;
			}

			ImageView view = new ImageView(icon);
			view.setFitWidth(iconSize);
			view.setFitHeight(iconSize);

			content.getChildren().add(view);
			AnchorPane.setLeftAnchor(view, 15.0);
			AnchorPane.setTopAnchor(view, (height - offset - iconSize) / 2.0);

			Label labelTitle = new Label(title);
			labelTitle.getStyleClass().add("notification-label-title");

			content.getChildren().add(labelTitle);
			AnchorPane.setLeftAnchor(labelTitle, 15.0 + iconSize + 25.0);
			AnchorPane.setTopAnchor(labelTitle, 15.0);

			Label labelDescription = new Label(description);
			labelDescription.getStyleClass().add("notification-label-description");
			labelDescription.setPrefWidth(width - offset - iconSize - 35.0);
			labelDescription.setWrapText(true);

			content.getChildren().add(labelDescription);
			AnchorPane.setLeftAnchor(labelDescription, 15.0 + iconSize + 25.0);
			AnchorPane.setTopAnchor(labelDescription, 50.0);

			Scene scene = new Scene(root, width, height);
			scene.setFill(Color.TRANSPARENT);

			stage.setScene(scene);
			stage.show();

			// propagates the focus back to the caller in order to prevent capturing all mouse and keyboard events
			if (owner != null) {
				owner.requestFocus();
			}

			// fade out effect
			timeline = new Timeline();
			KeyFrame key = new KeyFrame(Duration.millis(fadeOutTimeInMillis), new KeyValue(stage.getScene().getRoot().opacityProperty(), 0));
			timeline.getKeyFrames().add(key);
			timeline.setDelay(Duration.millis(hideAfterInMillis));
			timeline.setOnFinished(event -> {
				if (stage != null) {
					stage.close();
					stage = null;
				}
			});
			timeline.play();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
	}

	public boolean isOpen() {
		return stage != null;
	}
}
