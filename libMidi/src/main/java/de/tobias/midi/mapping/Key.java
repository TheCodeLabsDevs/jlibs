package de.tobias.midi.mapping;

import de.tobias.midi.feedback.Feedback;
import de.tobias.midi.feedback.FeedbackType;

public abstract class Key
{
	public abstract Feedback getFeedbackForType(FeedbackType feedbackType);
}
