package de.tobias.midi.action;

import de.tobias.midi.Mapping;
import de.tobias.midi.Midi;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.event.KeyEventHandler;
import de.tobias.midi.event.KeyEventType;
import de.tobias.midi.feedback.Feedback;
import de.tobias.midi.feedback.FeedbackType;
import de.tobias.midi.mapping.Key;
import de.tobias.midi.mapping.KeyType;
import de.tobias.midi.mapping.MidiKey;
import javafx.scene.input.KeyCode;

public class ActionKeyHandler implements KeyEventHandler
{
	@Override
	public void handleKeyEvent(KeyEvent keyEvent)
	{
		if(keyEvent.getKeyEventType() != KeyEventType.DOWN)
		{
			return;
		}

		Mapping mapping = Mapping.getCurrentMapping();
		if(mapping == null)
		{
			return;
		}

		if(keyEvent.getType() == KeyType.MIDI)
		{
			handleMidiEvent(keyEvent, mapping);
		}
		else if(keyEvent.getType() == KeyType.KEYBOARD)
		{
			handleKeyboardEvent(keyEvent, mapping);
		}
	}

	private void handleKeyboardEvent(KeyEvent keyEvent, Mapping mapping)
	{
		final Action action = mapping.getActionForKeyboardKey(KeyCode.values()[keyEvent.getKeyValue()]);
		ActionRegistry.getActionHandler(action.getActionType()).handle(keyEvent, action);
	}

	private void handleMidiEvent(KeyEvent keyEvent, Mapping mapping)
	{
		final Action action = mapping.getActionForMidiKey(keyEvent.getKeyValue());
		if(action != null)
		{
			final ActionHandler handler = ActionRegistry.getActionHandler(action.getActionType());
			FeedbackType feedbackType = handler.handle(keyEvent, action);

			if(Midi.getInstance().isModeSupported(Midi.Mode.OUTPUT))
			{
				for(Key key : action.getKeys())
				{
					if(key instanceof MidiKey)
					{
						MidiKey midiKey = (MidiKey) key;
						Feedback feedback = key.getFeedbackForType(feedbackType);
						if(feedback != null)
						{
							Midi.getInstance().sendMessage(feedback.getChannel(), midiKey.getValue(), feedback.getValue());
						}
					}
				}
			}
		}
	}
}
