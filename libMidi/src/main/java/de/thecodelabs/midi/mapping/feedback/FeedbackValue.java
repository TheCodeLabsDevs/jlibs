package de.thecodelabs.midi.mapping.feedback;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface FeedbackValue
{
}
