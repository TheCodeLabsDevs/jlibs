package de.tobias.midi.action;

import de.tobias.midi.Key;
import de.tobias.midi.Mapping;
import de.tobias.midi.Midi;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.event.KeyEventHandler;
import de.tobias.midi.event.KeyEventType;
import de.tobias.midi.feedback.Feedback;
import de.tobias.midi.feedback.FeedbackType;

public class ActionKeyHandler implements KeyEventHandler
{
	@Override
	public void handleMidiKeyEvent(KeyEvent keyEvent)
	{
		if (keyEvent.getKeyEventType() != KeyEventType.DOWN) {
			return;
		}

		Mapping mapping = Mapping.getCurrentMapping();
		if (mapping == null) {
			return;
		}

		final Action actionForKey = mapping.getActionForKey(keyEvent.getKeyValue());
		if(actionForKey != null)
		{
			final ActionHandler handler = ActionRegistry.getActionHandler(actionForKey.getActionType());
			FeedbackType feedbackType = handler.handle(keyEvent);

			if (Midi.getInstance().isModeSupported(Midi.Mode.OUTPUT)) {
				for (Key key : actionForKey.getKeys())
				{
					Feedback feedback = key.getFeedbackForType(feedbackType);
					if (feedback != null)
					{
						Midi.getInstance().sendMessage(feedback.getChannel(), key.getValue(), feedback.getValue());
					}
				}
			}
		}
	}
}
