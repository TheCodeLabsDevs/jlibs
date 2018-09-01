package de.tobias.midi;

import de.tobias.midi.feedback.Feedback;
import de.tobias.midi.feedback.FeedbackType;

import java.util.Objects;

public class Key
{
	private int value;

	private Feedback defaultFeedback;
	private Feedback eventFeedback;

	public Key()
	{
	}

	public Key(int value)
	{
		this.value = value;
	}

	public Key(int value, Feedback defaultFeedback, Feedback eventFeedback)
	{
		this.value = value;
		this.defaultFeedback = defaultFeedback;
		this.eventFeedback = eventFeedback;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	public Feedback getDefaultFeedback()
	{
		return defaultFeedback;
	}

	public void setDefaultFeedback(Feedback defaultFeedback)
	{
		this.defaultFeedback = defaultFeedback;
	}

	public Feedback getEventFeedback()
	{
		return eventFeedback;
	}

	public void setEventFeedback(Feedback eventFeedback)
	{
		this.eventFeedback = eventFeedback;
	}

	public Feedback getFeedbackForType(FeedbackType feedbackType)
	{
		switch(feedbackType)
		{
			case NONE:
				return null;
			case DEFAULT:
				return defaultFeedback;
			case EVENT:
				return eventFeedback;
		}
		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Key)) return false;
		Key key = (Key) o;
		return value == key.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(value);
	}
}
