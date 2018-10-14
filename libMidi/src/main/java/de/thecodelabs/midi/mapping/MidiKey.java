package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.Midi;
import de.thecodelabs.midi.MidiCommand;
import de.thecodelabs.midi.MidiCommandType;
import de.thecodelabs.midi.feedback.Feedback;
import de.thecodelabs.midi.feedback.FeedbackType;

import java.util.Objects;

public class MidiKey extends Key
{
	private byte value;

	private Feedback defaultFeedback;
	private Feedback eventFeedback;

	public MidiKey()
	{
	}

	public MidiKey(byte value)
	{
		this.value = value;
	}

	public MidiKey(byte value, Feedback defaultFeedback, Feedback eventFeedback)
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

	public byte getValue()
	{
		return value;
	}

	public void setValue(byte value)
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
			MidiCommand midiCommand = new MidiCommand(MidiCommandType.NOTE_ON, getValue(), feedback.getValue());
			Midi.getInstance().sendMessage(midiCommand);
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

	@Override
	public String toString()
	{
		return "MidiKey{" +
				"value=" + value +
				", defaultFeedback=" + defaultFeedback +
				", eventFeedback=" + eventFeedback +
				'}';
	}
}
