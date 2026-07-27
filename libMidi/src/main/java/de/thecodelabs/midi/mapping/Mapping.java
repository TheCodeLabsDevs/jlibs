package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.input.InputKey;

import java.util.*;
import java.util.function.Predicate;

public class Mapping
{
	private final Map<InputKey, Action> inputKeyActionMap;

	public Mapping()
	{
		inputKeyActionMap = new HashMap<>();
	}

	public void addInputKeyWithAction(InputKey inputKey, Action action)
	{
		inputKeyActionMap.put(inputKey, action);
	}

	public void removeInputKey(InputKey inputKey)
	{
		inputKeyActionMap.remove(inputKey);
	}

	public Action getAction(InputKey inputKey)
	{
		return inputKeyActionMap.get(inputKey);
	}

	@SuppressWarnings("unchecked")
	public <T extends InputKey> Optional<T> getInputKeyForPredicate(Predicate<InputKey> predicate)
	{
		return inputKeyActionMap.keySet().stream().filter(predicate).findFirst().map(key -> (T) key);
	}

	public Set<InputKey> getAllInputKeys()
	{
		return Collections.unmodifiableSet(inputKeyActionMap.keySet());
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
