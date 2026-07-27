package de.thecodelabs.midi.mapping.feedback;

public interface FeedbackProvider
{
	FeedbackValue getFeedbackValueForState(FeedbackState state);
}
