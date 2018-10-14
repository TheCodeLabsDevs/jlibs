package de.thecodelabs.midi.action;

import de.thecodelabs.midi.event.KeyEvent;
import de.thecodelabs.midi.feedback.FeedbackType;

public abstract class ActionHandler
{
	public abstract String actionType();

	public abstract FeedbackType handle(KeyEvent keyEvent, Action action);

	public abstract FeedbackType getCurrentFeedbackType(Action action);
}
