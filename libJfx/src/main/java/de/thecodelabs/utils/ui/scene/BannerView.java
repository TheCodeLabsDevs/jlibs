package de.thecodelabs.utils.ui.scene;

import de.thecodelabs.utils.ui.NVC;
import javafx.animation.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class BannerView extends VBox
{
	public enum Anchor
	{
		BOTTOM,
		LEFT,
		TOP,
		RIGHT
	}

	public static class BannerAnchor
	{
		private final Anchor anchor;
		private final double spacing;

		public BannerAnchor(Anchor anchor, double spacing)
		{
			this.anchor = anchor;
			this.spacing = spacing;
		}

		public Anchor getAnchor()
		{
			return anchor;
		}

		public double getSpacing()
		{
			return spacing;
		}
	}

	private final Node child;
	private Animation currentTimeline;

	public BannerView(NVC controller)
	{
		this(controller.getParent());
	}

	public BannerView(Node child)
	{
		super(child);

		this.child = child;
		this.setOpacity(0);
		VBox.setVgrow(this, Priority.ALWAYS);
		HBox.setHgrow(this, Priority.ALWAYS);
	}

	public void fade(long displayableDuration)
	{
		fade(displayableDuration, null);
	}

	public void fade(long displayableDuration, Runnable onFinish)
	{
		if(currentTimeline != null)
		{
			currentTimeline.stop();
		}

		SimpleIntegerProperty dummyProperty = new SimpleIntegerProperty();
		currentTimeline = new SequentialTransition(
				fadeInTimeline(),
				new Timeline(new KeyFrame(Duration.seconds(displayableDuration), new KeyValue(dummyProperty, 1))),
				fadeOutTimeline()
		);

		currentTimeline.setOnFinished(e -> {
			currentTimeline = null;
			if(onFinish != null)
			{
				onFinish.run();
			}
		});
		currentTimeline.playFromStart();
	}

	private Timeline fadeInTimeline()
	{
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.0),
				new KeyValue(this.opacityProperty(), 0)
		));
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.2),
				new KeyValue(this.opacityProperty(), 1)
		));
		return timeline;
	}

	public void fadeIn()
	{
		if(currentTimeline != null)
		{
			currentTimeline.stop();
		}

		this.currentTimeline = fadeInTimeline();

		currentTimeline.setOnFinished(e -> currentTimeline = null);
		currentTimeline.play();
	}

	private Timeline fadeOutTimeline()
	{
		Timeline timeline = new Timeline();
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.0),
				new KeyValue(this.opacityProperty(), 1)
		));
		timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(0.2),
				new KeyValue(this.opacityProperty(), 0)
		));
		return timeline;
	}

	public void fadeOut()
	{
		if(currentTimeline != null)
		{
			currentTimeline.stop();
		}

		this.currentTimeline = fadeOutTimeline();

		currentTimeline.setOnFinished(e -> currentTimeline = null);
		currentTimeline.play();
	}

	public boolean isShowing()
	{
		return getOpacity() != 0.0;
	}

	public void addToParent(AnchorPane parent, BannerAnchor... anchors)
	{
		parent.getChildren().add(this);

		for(BannerAnchor anchor : anchors)
		{
			addAnchor(anchor);
		}
	}

	private void addAnchor(BannerAnchor anchor)
	{
		switch(anchor.getAnchor())
		{
			case BOTTOM:
				AnchorPane.setBottomAnchor(this, anchor.getSpacing());
				break;
			case LEFT:
				AnchorPane.setLeftAnchor(this, anchor.getSpacing());
				break;
			case TOP:
				AnchorPane.setTopAnchor(this, anchor.getSpacing());
				break;
			case RIGHT:
				AnchorPane.setRightAnchor(this, anchor.getSpacing());
				break;
		}
	}
}
