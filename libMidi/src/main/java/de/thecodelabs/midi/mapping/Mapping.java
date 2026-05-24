package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.input.InputKey;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Mapping
{
	private final Map<InputKey, Action> inputKeyActionMap;

	public Mapping()
	{
		inputKeyActionMap = new HashMap<>();
	}

	public void addAction(InputKey inputKey, Action action)
	{
		inputKeyActionMap.put(inputKey, action);
	}

	public Action getAction(InputKey inputKey)
	{
		return inputKeyActionMap.get(inputKey);
	}

	Map<InputKey, Action> getInputKeyActionMap()
	{
		return Collections.unmodifiableMap(inputKeyActionMap);
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null || getClass() != o.getClass()) return false;
		Mapping mapping = (Mapping) o;
		return Objects.equals(inputKeyActionMap, mapping.inputKeyActionMap);
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(inputKeyActionMap);
	}

	@Override
	public String toString()
	{
		return "Mapping{" +
		       "inputKeyActionMap=" + inputKeyActionMap +
		       '}';
	}
}
