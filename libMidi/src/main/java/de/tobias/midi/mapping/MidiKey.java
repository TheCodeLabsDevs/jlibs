package de.tobias.midi.mapping;

import de.tobias.midi.Midi;
import de.tobias.midi.feedback.Feedback;
import de.tobias.midi.feedback.FeedbackType;

import java.util.Objects;

public class MidiKey extends Key
{
	private int value;

	private Feedback defaultFeedback;
	private Feedback eventFeedback;

	public MidiKey()
	{
	}

	public MidiKey(int value)
	{
		this.value = value;
	}

	public MidiKey(int value, Feedback defaultFeedback, Feedback eventFeedback)
	{
		this.value = value;
		this.defaultFeedback = defaultFeedback;
		this.eventFeedback = eventFeedback;
	}

	@Override
	public KeyType getType()
	{
		return KeyType.MIDI;
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

	@Override
	public Feedback getFeedbackForType(FeedbackType feedbackType)
	{
		if (feedbackType == null) {
			return null;
		}

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

	public void sendFeedback(FeedbackType feedbackType)
	{
		Feedback feedback = getFeedbackForType(feedbackType);
		if(feedback != null)
		{
			Midi.getInstance().sendMessage(feedback.getChannel(), getValue(), feedback.getValue());
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof MidiKey)) return false;
		MidiKey key = (MidiKey) o;
		return value == key.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(value);
	}
}
