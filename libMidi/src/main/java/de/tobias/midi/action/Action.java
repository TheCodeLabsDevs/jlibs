package de.tobias.midi.action;

import de.tobias.midi.mapping.MidiKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Action
{
	private List<MidiKey> keys;
	private String actionType;

	private Map<String, String> payload;

	public Action()
	{
		this("");
	}

	public Action(String actionType)
	{
		this(new ArrayList<>(), actionType);
	}

	public Action(List<MidiKey> keys, String actionType)
	{
		this.keys = keys;
		this.actionType = actionType;
		this.payload = new HashMap<>();
	}

	public Action(List<MidiKey> keys, String actionType, Map<String, String> payload)
	{
		this.keys = keys;
		this.actionType = actionType;
		this.payload = payload;
	}

	public List<MidiKey> getKeys()
	{
		return keys;
	}

	public void setKeys(List<MidiKey> keys)
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

	public Map<String, String> getPayload()
	{
		return payload;
	}

	public void setPayload(Map<String, String> payload)
	{
		this.payload = payload;
	}
}
