package de.tobias.midi;

import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionKeyHandler;
import de.tobias.midi.event.KeyEventDispatcher;
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

	public Action getActionForKey(int key)
	{
		for(Action action : actions)
		{
			for(MidiKey actionKey : action.getKeys())
			{
				if(actionKey.getValue() == key)
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

	public Optional<MidiKey> getFirstKeyForAction(Action action)
	{
		if(action.getKeys().size() > 0)
		{
			return Optional.of(action.getKeys().get(0));
		}
		else
		{
			return Optional.empty();
		}
	}

	public MidiKey getFirstKeyOrCreateForAction(Action action)
	{
		return getFirstKeyForAction(action).orElseGet(() -> {
			MidiKey newKey = new MidiKey();
			action.getKeys().add(newKey);
			return newKey;
		});
	}
}
