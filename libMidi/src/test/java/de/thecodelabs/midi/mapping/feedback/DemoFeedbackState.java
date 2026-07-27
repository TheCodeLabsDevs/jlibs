package de.thecodelabs.midi.mapping.feedback;


import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("demo-feedback-state")
public enum DemoFeedbackState implements FeedbackState
{
	NORMAL,
	PLAYING
}
