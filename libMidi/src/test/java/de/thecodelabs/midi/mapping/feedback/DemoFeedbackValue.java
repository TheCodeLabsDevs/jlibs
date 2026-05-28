package de.thecodelabs.midi.mapping.feedback;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("demo-feedback-value")
public record DemoFeedbackValue(String value) implements FeedbackValue
{
	@JsonCreator
	public DemoFeedbackValue(@JsonProperty("value") String value)
	{
		this.value = value;
	}

}
