package de.tobias.midi.action;

import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

public abstract class ActionHandler
{
	public abstract String actionType();

	public abstract FeedbackType handle(KeyEvent keyEvent);
}
