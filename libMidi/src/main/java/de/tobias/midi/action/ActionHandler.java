package de.tobias.midi.action;

import de.tobias.midi.event.KeyEvent;

public abstract class ActionHandler
{
	public abstract String actionType();

	public abstract void handle(KeyEvent keyEvent);
}
