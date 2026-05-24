package de.thecodelabs.midi.mapping.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.thecodelabs.midi.feedback.Feedback;

import java.util.Objects;

public class MidiInputKey implements InputKey
{
	private byte value;

	private Feedback defaultFeedback;
	private Feedback eventFeedback;
	private Feedback warningFeedback;

	public MidiInputKey(byte value)
	{
		this.value = value;
	}

	public MidiInputKey(byte value, Feedback defaultFeedback, Feedback eventFeedback)
	{
		this(value, defaultFeedback, eventFeedback, null);
	}

	@JsonCreator
	public MidiInputKey(@JsonProperty("value") byte value,
	                    @JsonProperty("defaultFeedback") Feedback defaultFeedback,
	                    @JsonProperty("eventFeedback") Feedback eventFeedback,
	                    @JsonProperty("warningFeedback") Feedback warningFeedback)
	{
		this.value = value;
		this.defaultFeedback = defaultFeedback;
		this.eventFeedback = eventFeedback;
		this.warningFeedback = warningFeedback;
	}

	public byte getValue()
	{
		return value;
	}

	public Feedback getDefaultFeedback()
	{
		return defaultFeedback;
	}

	public Feedback getEventFeedback()
	{
		return eventFeedback;
	}

	public Feedback getWarningFeedback()
	{
		return warningFeedback;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null || getClass() != o.getClass()) return false;
		final MidiInputKey that = (MidiInputKey) o;
		return value == that.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(value);
	}

	@Override
	public String toString()
	{
		return "MidiInputKey{" +
		       "value=" + value +
		       ", defaultFeedback=" + defaultFeedback +
		       ", eventFeedback=" + eventFeedback +
		       ", warningFeedback=" + warningFeedback +
		       '}';
	}
}
