package de.thecodelabs.midi.mapping.listener;

import de.thecodelabs.midi.event.KeyInputEvent;
import de.thecodelabs.midi.event.KeyInputType;
import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.action.ActionHandler;
import de.thecodelabs.midi.mapping.action.ActionHandlerResolver;
import de.thecodelabs.midi.mapping.input.InputKey;
import de.thecodelabs.midi.mapping.input.KeyboardInputKey;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.Optional;

public class KeyboardMappingListener implements EventHandler<KeyEvent>
{
	private final Mapping mapping;
	private final ActionHandlerResolver actionHandlerResolver;

	public KeyboardMappingListener(Mapping mapping, ActionHandlerResolver actionHandlerResolver)
	{
		this.mapping = mapping;
		this.actionHandlerResolver = actionHandlerResolver;
	}

	@Override
	public void handle(KeyEvent event)
	{
		if(!event.isShortcutDown())
		{
			final KeyCode code;
			final KeyInputType type;

			if(event.getEventType() == KeyEvent.KEY_PRESSED)
			{
				code = event.getCode();
				type = KeyInputType.DOWN;
			}
			else if(event.getEventType() == KeyEvent.KEY_RELEASED)
			{
				code = event.getCode();
				type = KeyInputType.UP;
			}
			else
			{
				return;
			}

			final Optional<InputKey> midiKeyOptional = mapping.getInputKeyForPredicate(inputKey ->
					inputKey instanceof KeyboardInputKey keyboardInputKey
					&& keyboardInputKey.code() == code);
			if(midiKeyOptional.isEmpty())
			{
				return;
			}

			final Action action = mapping.getAction(midiKeyOptional.get());
			final ActionHandler actionHandler = actionHandlerResolver.resolve(action);
			if(actionHandler == null)
			{
				return;
			}

			final KeyInputEvent keyEvent = new KeyInputEvent(midiKeyOptional.get(), type);
			actionHandler.handleAction(keyEvent, action);
		}

	}
}
