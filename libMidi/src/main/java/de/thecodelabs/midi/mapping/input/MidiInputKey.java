package de.thecodelabs.midi.mapping.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.thecodelabs.midi.mapping.feedback.FeedbackState;
import de.thecodelabs.midi.mapping.feedback.FeedbackValue;
import de.thecodelabs.midi.mapping.feedback.FeedbackValueMapDeserializer;
import de.thecodelabs.midi.mapping.feedback.FeedbackValueMapSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName("midi")
public class MidiInputKey implements InputKey
{
	private final byte value;
	private Map<FeedbackState, FeedbackValue> feedbackValues = new HashMap<>();

	public MidiInputKey(byte value)
	{
		this.value = value;
	}

	@JsonCreator
	public MidiInputKey(@JsonProperty("value") byte value,
						@JsonDeserialize(using = FeedbackValueMapDeserializer.class)
						@JsonProperty("feedbackValues") Map<FeedbackState, FeedbackValue> feedbackValues)
	{
		this.value = value;
		this.feedbackValues = feedbackValues != null ? new HashMap<>(feedbackValues) : new HashMap<>();
	}

	public byte getValue()
	{
		return value;
	}

	@JsonSerialize(using = FeedbackValueMapSerializer.class)
	public Map<FeedbackState, FeedbackValue> getFeedbackValues()
	{
		return feedbackValues;
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
}
