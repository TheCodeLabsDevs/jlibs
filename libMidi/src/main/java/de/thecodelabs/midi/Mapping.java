package de.thecodelabs.midi;

import de.thecodelabs.midi.action.Action;
import de.thecodelabs.midi.action.ActionHandler;
import de.thecodelabs.midi.action.ActionKeyHandler;
import de.thecodelabs.midi.action.ActionRegistry;
import de.thecodelabs.midi.event.KeyEventDispatcher;
import de.thecodelabs.midi.feedback.FeedbackType;
import de.thecodelabs.midi.mapping.KeyboardKey;
import de.thecodelabs.midi.mapping.MidiKey;
import javafx.scene.input.KeyCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Mapping
{
	static
	{
		KeyEventDispatcher.registerKeyEventHandler(new ActionKeyHandler());
	}

	private transient static Mapping currentMapping;

	public static Mapping getCurrentMapping()
	{
		return currentMapping;
	}

	public static void setCurrentMapping(Mapping currentMapping)
	{
		Mapping.currentMapping = currentMapping;
	}

	private List<Action> actions;

	public Mapping()
	{
		this(new ArrayList<>());
	}

	public Mapping(List<Action> actions)
	{
		this.actions = actions;
	}

	public void addAction(Action action)
	{
		this.actions.add(action);
	}

	public void removeAction(Action action)
	{
		this.actions.remove(action);
	}

	public List<Action> getActions()
	{
		return actions;
	}

	public void showFeedback()
	{
		for(Action action : getActions())
		{
			ActionHandler handler = ActionRegistry.getActionHandler(action.getActionType());
			final FeedbackType currentFeedbackType = handler.getCurrentFeedbackType(action);

			for(MidiKey key : action.getKeysForType(MidiKey.class))
			{
				key.sendFeedback(currentFeedbackType);
			}
		}
	}

	// TODO Extract special launchpad commands
	public void clearFeedback()
	{
		final int maxMainKeyNumber = 89;

		for(byte i = 11; i <= maxMainKeyNumber; i++)
		{
			// Node_On = 144
			MidiCommand midiCommand = new MidiCommand(MidiCommandType.NOTE_ON, i, (byte) 0);
			Midi.getInstance().sendMessage(midiCommand);
		}

		// Obere Reihe an Tasten
		final int liveKeyMin = 104;
		final int liveKeyMax = 111;

		for(byte i = liveKeyMin; i <= liveKeyMax; i++)
		{
			// Control_Change = 176
			MidiCommand midiCommand = new MidiCommand(MidiCommandType.CONTROL_CHANGE, i, (byte) 0);
			Midi.getInstance().sendMessage(midiCommand);
		}
	}

	public Action getActionForMidiKey(int key)
	{
		for(Action action : actions)
		{
			for(MidiKey actionKey : action.getKeysForType(MidiKey.class))
			{
				if(actionKey.getValue() == key)
				{
					return action;
				}
			}
		}
		return null;
	}

	public Action getActionForKeyboardKey(KeyCode key)
	{
		for(Action action : actions)
		{
			for(KeyboardKey actionKey : action.getKeysForType(KeyboardKey.class))
			{
				if(actionKey.getCode() == key)
				{
					return action;
				}
			}
		}
		return null;
	}

	public List<Action> getActionsForType(String type)
	{
		List<Action> result = new ArrayList<>();
		for(Action action : actions)
		{
			if(action.getActionType().equals(type))
			{
				result.add(action);
			}
		}
		return result;
	}

	public Action getFirstActionOrCreateForType(String type)
	{
		final List<Action> actions = Mapping.getCurrentMapping().getActionsForType(type);
		if(actions.size() == 0)
		{
			final Action action = new Action(type);
			Mapping.getCurrentMapping().addAction(action);
			return action;
		}
		return actions.get(0);
	}

	public Optional<MidiKey> getFirstMidiKeyForAction(Action action)
	{
		if(action.getKeys().size() > 0)
		{
			return action.getKeys().stream().filter(key -> key instanceof MidiKey).map(key -> (MidiKey) key).findAny();
		}
		else
		{
			return Optional.empty();
		}
	}

	public Optional<KeyboardKey> getFirstKeyboardKeyForAction(Action action)
	{
		if(action.getKeys().size() > 0)
		{
			return action.getKeys().stream().filter(key -> key instanceof KeyboardKey).map(key -> (KeyboardKey) key).findAny();
		}
		else
		{
			return Optional.empty();
		}
	}

	public MidiKey getFirstMidiKeyOrCreateForAction(Action action)
	{
		return getFirstMidiKeyForAction(action).orElseGet(() -> {
			MidiKey newKey = new MidiKey();
			action.getKeys().add(newKey);
			return newKey;
		});
	}

	public KeyboardKey getFirstKeyboardKeyOrCreateForAction(Action action)
	{
		return getFirstKeyboardKeyForAction(action).orElseGet(() -> {
			KeyboardKey newKey = new KeyboardKey();
			action.getKeys().add(newKey);
			return newKey;
		});
	}

	@Override
	public String toString()
	{
		return "Mapping{" +
				"actions=" + actions +
				'}';
	}
}