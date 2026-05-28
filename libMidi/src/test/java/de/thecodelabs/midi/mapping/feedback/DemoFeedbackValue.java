package de.thecodelabs.midi.mapping.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("demo-feedback-value")
public record DemoFeedbackValue(int value) implements FeedbackValue
{
	@JsonCreator
	public DemoFeedbackValue(@JsonProperty("value") int value)
	{
		this.value = value;
	}

}
