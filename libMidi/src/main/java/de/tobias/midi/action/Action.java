package de.tobias.midi.action;

import de.tobias.midi.Key;

import java.util.ArrayList;
import java.util.List;

public class Action
{
	private List<Key> keys;
	private String actionType;

	public Action()
	{
		keys = new ArrayList<>();
		actionType = "";
	}

	public Action(List<Key> keys, String actionType)
	{
		this.keys = keys;
		this.actionType = actionType;
	}

	public List<Key> getKeys()
	{
		return keys;
	}

	public void setKeys(List<Key> keys)
	{
		this.keys = keys;
	}

	public String getActionType()
	{
		return actionType;
	}

	public void setActionType(String actionType)
	{
		this.actionType = actionType;
	}
}
