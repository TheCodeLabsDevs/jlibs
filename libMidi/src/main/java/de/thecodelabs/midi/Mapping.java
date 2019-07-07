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

import java.util.*;

public class Mapping
{
	static
	{
		KeyEventDispatcher.registerKeyEventHandler(new ActionKeyHandler());
	}

	private static Mapping currentMapping;
	private Map<Integer, Action> midiCache = new HashMap<>();

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
		if(midiCache.containsKey(key))
		{
			final Action action = midiCache.get(key);
			// Validate cache entry
			if(action.getKeysForType(MidiKey.class).parallelStream().anyMatch(k -> k.getValue() == key))
			{
				return action;
			}
		}

		final Action action = actions.parallelStream()
				.filter(a ->
						a.getKeysForType(MidiKey.class).parallelStream()
								.anyMatch(actionKey -> actionKey.getValue() == key)
				)
				.findFirst().orElse(null);

		if(action != null)
		{
			midiCache.put(key, action);
		}
		return action;
	}

	public Action getActionForKeyboardKey(KeyCode key)
	{
		return actions.parallelStream()
				.filter(action ->
						action.getKeysForType(KeyboardKey.class).parallelStream()
								.anyMatch(actionKey -> actionKey.getCode() == key)
				)
				.findFirst().orElse(null);
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
		if(actions.isEmpty())
		{
			final Action action = new Action(type);
			Mapping.getCurrentMapping().addAction(action);
			return action;
		}
		return actions.get(0);
	}

	public Optional<MidiKey> getFirstMidiKeyForAction(Action action)
	{
		if(!action.getKeys().isEmpty())
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
		if(!action.getKeys().isEmpty())
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
