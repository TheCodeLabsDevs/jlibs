package de.thecodelabs.utils.ui.notification;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Notification
{
	private static final double CONTENT_PADDING = 5.0;
	private static final int DEFAULT_HIDE_AFTER_IN_MILLIS = 4000;
	private static final int DEFAULT_FADE_OUT_TIME_IN_MILLIS = 1000;
	private static final String DEFAULT_STYLESHEET = "notification/style/defaultNotificationStyle.css";

	private int width;
	private int height;
	private int offset;
	private int iconSize;
	private int hideAfterInMillis;
	private int fadeOutTimeInMillis;
	private Stage owner;
	private Image defaultIcon;
	private String styleSheet;

	private List<NotificationElement> elementQueue = new ArrayList<>();

	public Notification(int width, int height, int offset, int iconSize, int hideAfterInMillis, int fadeOutTimeInMillis, Stage owner, Image defaultIcon, String styleSheet)
	{
		this.width = width;
		this.height = height;
		this.offset = offset;
		this.iconSize = iconSize;

		this.hideAfterInMillis = hideAfterInMillis;
		if(this.hideAfterInMillis == 0)
		{
			this.hideAfterInMillis = DEFAULT_HIDE_AFTER_IN_MILLIS;
		}

		this.fadeOutTimeInMillis = fadeOutTimeInMillis;
		if(this.fadeOutTimeInMillis == 0)
		{
			this.fadeOutTimeInMillis = DEFAULT_FADE_OUT_TIME_IN_MILLIS;
		}

		this.owner = owner;
		this.defaultIcon = defaultIcon;
		this.styleSheet = Objects.requireNonNullElse(styleSheet, DEFAULT_STYLESHEET);
	}

	public NotificationElement show(String title, String description)
	{
		return show(title, description, defaultIcon);
	}

	public NotificationElement show(String title, String description, Image icon)
	{
		// use default icon if icon is missing
		if(icon == null)
		{
			icon = defaultIcon;
		}

		try
		{
			final Stage stage = new Stage();
			stage.initStyle(StageStyle.TRANSPARENT);
			stage.setAlwaysOnTop(true);

			final Optional<Point2D> positionOptional = calculatePosition(0);
			if(positionOptional.isEmpty())
			{
				throw new RuntimeException("Could not calculate positio for notification stage");
			}

			final Point2D position = positionOptional.get();
			stage.setX(position.getX());
			stage.setY(position.getY());

			final NotificationContent content = new NotificationContent(width, height, CONTENT_PADDING, icon, iconSize, title, description);
			final AnchorPane root = createRootPane();
			root.getChildren().add(content);
			AnchorPane.setLeftAnchor(content, CONTENT_PADDING);
			AnchorPane.setTopAnchor(content, CONTENT_PADDING);

			final Scene scene = new Scene(root, width, height);
			scene.setFill(Color.TRANSPARENT);
			stage.setScene(scene);
			stage.show();

			NotificationElement element = new NotificationElement(stage);
			Timeline timeline = createTimeline(element, hideAfterInMillis, fadeOutTimeInMillis);
			element.setTimeline(timeline);
			content.getCloseButton().setOnAction(event -> close(element, true));
			elementQueue.add(element);
			reorganizeElements();

			// fade out effect
			timeline.play();

			// propagates the focus back to the caller in order to prevent capturing all mouse and keyboard events
			if(owner != null)
			{
				owner.requestFocus();
			}

			return element;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	private AnchorPane createRootPane()
	{
		AnchorPane root = new AnchorPane();
		root.setPrefWidth(width);
		root.setPrefHeight(height);
		root.setBackground(Background.EMPTY);
		root.getStylesheets().add(styleSheet);
		return root;
	}

	private Timeline createTimeline(NotificationElement element, int hideAfter, int fadeOutTime)
	{
		Timeline timeline = new Timeline();
		KeyFrame key = new KeyFrame(Duration.millis(fadeOutTime), new KeyValue(element.getStage().getScene().getRoot().opacityProperty(), 0));
		timeline.getKeyFrames().add(key);
		timeline.setDelay(Duration.millis(hideAfter));
		timeline.setOnFinished(event -> {
			if(elementQueue.contains(element))
			{
				close(element, false);
			}
		});
		return timeline;
	}

	public void close(NotificationElement element, boolean fadeOut)
	{
		if(element == null)
		{
			return;
		}

		if(fadeOut)
		{
			createTimeline(element, 0, fadeOutTimeInMillis).play();
			return;
		}

		element.close();
		elementQueue.remove(element);
		reorganizeElements();
	}

	private void reorganizeElements()
	{
		for(int i = 0; i < elementQueue.size(); i++)
		{
			final Stage stage = elementQueue.get(i).getStage();
			final Optional<Point2D> positionOptional = calculatePosition(elementQueue.size() - i);
			if(positionOptional.isEmpty())
			{
				// close oldest element
				close(elementQueue.get(0), false);
				return;
			}
			stage.setY(positionOptional.get().getY());
		}
	}

	private Optional<Point2D> calculatePosition(int index)
	{
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		final double screenWidth = primaryScreenBounds.getMaxX();
		final double screenHeight = primaryScreenBounds.getMaxY();

		final double stageWidth = width + offset;
		final double x = screenWidth - stageWidth;

		final double stageHeight = height + offset;
		final double y = screenHeight - index * stageHeight;

		// stage would be above screen
		if(y < 0)
		{
			return Optional.empty();
		}

		return Optional.of(new Point2D(x, y));
	}

	public boolean isOpen(NotificationElement element)
	{
		return elementQueue.contains(element);
	}

	public boolean isAnyOpen()
	{
		return !elementQueue.isEmpty();
	}

	public List<NotificationElement> getElementQueue()
	{
		return elementQueue;
	}
}
