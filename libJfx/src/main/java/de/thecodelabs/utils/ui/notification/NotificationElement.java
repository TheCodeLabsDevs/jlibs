package de.thecodelabs.utils.ui.notification;

import javafx.animation.Timeline;
import javafx.stage.Stage;

import java.util.Objects;

public class NotificationElement
{
	private Stage stage;
	private Timeline timeline;

	public NotificationElement(Stage stage)
	{
		this.stage = stage;
	}

	public Stage getStage()
	{
		return stage;
	}

	public Timeline getTimeline()
	{
		return timeline;
	}

	public void setTimeline(Timeline timeline)
	{
		this.timeline = timeline;
	}

	public void close()
	{
		stage.close();
		timeline.stop();
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		NotificationElement that = (NotificationElement) o;
		return Objects.equals(stage, that.stage) &&
				Objects.equals(timeline, that.timeline);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(stage, timeline);
	}

	@Override
	public String toString()
	{
		return "NotificationElement{" +
				"stage=" + stage +
				", timeline=" + timeline +
				'}';
	}
}
