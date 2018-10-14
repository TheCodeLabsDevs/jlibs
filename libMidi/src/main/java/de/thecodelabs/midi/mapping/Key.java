package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.feedback.Feedback;
import de.thecodelabs.midi.feedback.FeedbackType;

public abstract class Key
{
	public abstract KeyType getType();

	public abstract Feedback getFeedbackForType(FeedbackType feedbackType);
}
