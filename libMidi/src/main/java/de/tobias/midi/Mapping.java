package de.tobias.midi;

import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionKeyHandler;
import de.tobias.midi.event.KeyEventDispatcher;
import de.tobias.midi.mapping.Key;
import de.tobias.midi.mapping.MidiKey;

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

	public Action getActionForMidiKey(int key)
	{
		for(Action action : actions)
		{
			for(Key actionKey : action.getKeys())
			{
				if(actionKey instanceof MidiKey)
				{
					if(((MidiKey) actionKey).getValue() == key)
					{
						return action;
					}
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

	public MidiKey getFirstMidiKeyOrCreateForAction(Action action)
	{
		return getFirstMidiKeyForAction(action).orElseGet(() -> {
			MidiKey newKey = new MidiKey();
			action.getKeys().add(newKey);
			return newKey;
		});
	}
}
