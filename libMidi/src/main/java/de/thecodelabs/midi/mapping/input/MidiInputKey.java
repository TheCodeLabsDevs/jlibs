package de.thecodelabs.midi.mapping.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.thecodelabs.midi.mapping.feedback.MidiFeedback;

import java.util.Objects;

public class MidiInputKey implements InputKey
{
	private byte value;

	private MidiFeedback defaultFeedback;
	private MidiFeedback eventFeedback;
	private MidiFeedback warningFeedback;

	public MidiInputKey(byte value)
	{
		this.value = value;
	}

	public MidiInputKey(byte value, MidiFeedback defaultFeedback, MidiFeedback eventFeedback)
	{
		this(value, defaultFeedback, eventFeedback, null);
	}

	@JsonCreator
	public MidiInputKey(@JsonProperty("value") byte value,
	                    @JsonProperty("defaultFeedback") MidiFeedback defaultFeedback,
	                    @JsonProperty("eventFeedback") MidiFeedback eventFeedback,
	                    @JsonProperty("warningFeedback") MidiFeedback warningFeedback)
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

	public MidiFeedback getDefaultFeedback()
	{
		return defaultFeedback;
	}

	public MidiFeedback getEventFeedback()
	{
		return eventFeedback;
	}

	public MidiFeedback getWarningFeedback()
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
