package de.thecodelabs.midi.mapping.action;

import de.thecodelabs.midi.event.KeyInputEvent;
import de.thecodelabs.midi.mapping.feedback.FeedbackState;

public interface ActionHandler
{
	FeedbackState handleAction(KeyInputEvent event, Action action);

	FeedbackState getCurrentState(Action action);
}
