package de.thecodelabs.utils.ui.notification;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.*;

public class Notification
{
	private int width;
	private int height;
	private int offset;
	private int iconSize;

	private String title;
	private String description;
	private Image icon;
	private Image defaultIcon;
	private int hideAfterInMillis;
	private static final int DEFAULT_HIDE_AFTER_IN_MILLIS = 4000;
	private int fadeOutTimeInMillis;
	private static final int DEFAULT_FADE_OUT_TIME_IN_MILLIS = 1000;

	private Stage owner;
	private String styleSheet;
	private List<NotificationElement> elementQueue = new ArrayList<>();

	public Notification(int width, int height, int offset, int iconSize, int hideAfterInMillis, int fadeOutTimeInMillis, Stage owner, Image defaultIcon, String styleSheet)
	{
		this.width = width;
		this.height = height;
		this.offset = offset;
		this.iconSize = iconSize;
		this.hideAfterInMillis = hideAfterInMillis;
		this.fadeOutTimeInMillis = fadeOutTimeInMillis;
		this.owner = owner;
		this.defaultIcon = defaultIcon;
		this.styleSheet = Objects.requireNonNullElse(styleSheet, "notification/style/defaultNotificationStyle.css");
	}

	public void close(NotificationElement element, boolean fadeOut)
	{
		if(element != null)
		{
			if(fadeOut)
			{
				createTimeline(element, 0, fadeOutTimeInMillis).play();
			}
			else
			{
				element.close();
				elementQueue.remove(element);
				reorganizeElements();
			}
		}
	}

	private Optional<Point2D> calculatePosition(int position)
	{
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		final double screenWidth = primaryScreenBounds.getMaxX();
		final double screenHeight = primaryScreenBounds.getMaxY();

		final double stageWidth = width + offset;
		final double x = screenWidth - stageWidth;

		final double stageHeight = height + offset;
		final double y = screenHeight - position * stageHeight;

		// stage would be above screen
		if(y < 0)
		{
			return Optional.empty();
		}

		return Optional.of(new Point2D(x, y));
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

	public NotificationElement show()
	{
		try
		{
			if(hideAfterInMillis == 0)
			{
				hideAfterInMillis = DEFAULT_HIDE_AFTER_IN_MILLIS;
			}

			if(fadeOutTimeInMillis == 0)
			{
				fadeOutTimeInMillis = DEFAULT_FADE_OUT_TIME_IN_MILLIS;
			}

			Stage stage = new Stage();

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

			final AnchorPane root = createRootPane();

			// use default icon if icon is missing
			if(icon == null)
			{
				icon = defaultIcon;
			}
			final NotificationContent content = new NotificationContent(width, height, offset, icon, iconSize, title, description);
			root.getChildren().add(content);
			AnchorPane.setLeftAnchor(content, 5.0);
			AnchorPane.setTopAnchor(content, 5.0);

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

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setIcon(Image icon)
	{
		this.icon = icon;
	}

	public boolean isOpen(Stage stage)
	{
		return elementQueue.contains(stage);
	}

	public boolean isAnyOpen()
	{
		return !elementQueue.isEmpty();
	}
}
