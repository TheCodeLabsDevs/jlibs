package de.tobias.midi.action;

import de.tobias.midi.mapping.Key;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Action
{
	private List<Key> keys;
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

	public Action(List<Key> keys, String actionType)
	{
		this.keys = keys;
		this.actionType = actionType;
		this.payload = new HashMap<>();
	}

	public Action(List<Key> keys, String actionType, Map<String, String> payload)
	{
		this.keys = keys;
		this.actionType = actionType;
		this.payload = payload;
	}

	public List<Key> getKeys()
	{
		return keys;
	}

	public <T extends Key> List<T> getKeysForType(Class<T> clazz)
	{
		return keys.stream().filter(key -> key.getClass().isAssignableFrom(clazz)).map(clazz::cast).collect(Collectors.toList());
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

	public Map<String, String> getPayload()
	{
		return payload;
	}

	public void setPayload(Map<String, String> payload)
	{
		this.payload = payload;
	}
}
