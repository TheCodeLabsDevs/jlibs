package de.thecodelabs.midi.mapping.listener;

import de.thecodelabs.midi.event.KeyInputEvent;
import de.thecodelabs.midi.event.KeyInputType;
import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.action.ActionHandler;
import de.thecodelabs.midi.mapping.action.ActionHandlerResolver;
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
			final KeyCode code = event.getCode();
			final KeyInputType type;

			if(event.getEventType() == KeyEvent.KEY_PRESSED)
			{
				type = KeyInputType.DOWN;
			}
			else if(event.getEventType() == KeyEvent.KEY_RELEASED)
			{
				type = KeyInputType.UP;
			}
			else
			{
				return;
			}

			final Optional<KeyboardInputKey> keyboardKeyOptional = mapping.getInputKeyForPredicate(inputKey ->
					inputKey instanceof KeyboardInputKey keyboardInputKey
					&& keyboardInputKey.code() == code);
			if(keyboardKeyOptional.isEmpty())
			{
				return;
			}

			final Action action = mapping.getAction(keyboardKeyOptional.get());
			if(action == null)
			{
				return;
			}
			final ActionHandler actionHandler = actionHandlerResolver.resolve(action);
			if(actionHandler == null)
			{
				return;
			}

			final KeyInputEvent keyEvent = new KeyInputEvent(keyboardKeyOptional.get(), type);
			actionHandler.handleAction(keyEvent, action);
		}

	}
}
