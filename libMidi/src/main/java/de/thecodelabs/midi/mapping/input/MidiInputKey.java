package de.thecodelabs.midi.mapping.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import de.thecodelabs.midi.mapping.feedback.*;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonTypeName("midi")
public record MidiInputKey(byte value, Map<FeedbackState, FeedbackValue> feedbackValues) implements InputKey, FeedbackProvider
{
	public MidiInputKey(byte value)
	{
		this(value, new HashMap<>());
	}

	@JsonCreator
	public MidiInputKey(@JsonProperty("value") byte value,
	                    @JsonDeserialize(using = FeedbackValueMapDeserializer.class)
						@JsonProperty("feedbackValues") Map<FeedbackState, FeedbackValue> feedbackValues)
	{
		this.value = value;
		this.feedbackValues = feedbackValues != null ? new HashMap<>(feedbackValues) : new HashMap<>();
	}


	@Override
	@JsonSerialize(using = FeedbackValueMapSerializer.class)
	public Map<FeedbackState, FeedbackValue> feedbackValues()
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

	@Override
	public FeedbackValue getFeedbackValueForState(FeedbackState state)
	{
		return feedbackValues.get(state);
	}
}
