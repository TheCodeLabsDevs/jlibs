package de.tobias.midi;

import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionKeyHandler;
import de.tobias.midi.event.KeyEventDispatcher;

import java.util.ArrayList;
import java.util.List;

public class Mapping
{
	static {
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

	public Action getActionForKey(int key)
	{
		for(Action action : actions)
		{
			for(Key actionKey : action.getKeys())
			{
				if(actionKey.getValue() == key)
				{
					return action;
				}
			}
		}
		return null;
	}
}
