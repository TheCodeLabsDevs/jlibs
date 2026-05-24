package de.thecodelabs.midi.mapping.action;

import de.thecodelabs.midi.event.KeyInputEvent;

public interface ActionHandler
{
	void handleAction(KeyInputEvent event, Action action);
}
