package de.tobias.midi.action;

import de.tobias.midi.Mapping;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.event.KeyEventHandler;

public class ActionKeyHandler implements KeyEventHandler
{
	@Override
	public void handleMidiKeyEvent(KeyEvent keyEvent)
	{
		Mapping mapping = Mapping.getCurrentMapping();
		if (mapping == null) {
			return;
		}

		final Action actionForKey = mapping.getActionForKey(keyEvent.getKeyValue());
		if(actionForKey != null)
		{
			final ActionHandler handler = ActionRegistry.getActionHandler(actionForKey.getActionType());
			handler.handle(keyEvent);
		}
	}
}
