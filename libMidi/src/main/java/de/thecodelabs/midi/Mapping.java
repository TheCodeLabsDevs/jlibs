package de.thecodelabs.midi;

import de.thecodelabs.midi.action.Action;
import de.thecodelabs.midi.action.ActionKeyHandler;
import de.thecodelabs.midi.event.KeyEventDispatcher;
import de.thecodelabs.midi.mapping.KeyboardKey;
import de.thecodelabs.midi.mapping.MidiKey;
import javafx.scene.input.KeyCode;

import java.util.*;
import java.util.stream.Collectors;

public class Mapping
{
	static
	{
		KeyEventDispatcher.registerKeyEventHandler(new ActionKeyHandler());
	}

	private static Mapping currentMapping;

	public static Mapping getCurrentMapping()
	{
		return currentMapping;
	}

	public static void setCurrentMapping(Mapping currentMapping)
	{
		Mapping.currentMapping = currentMapping;
	}

	private UUID id;
	private String name;
	private List<Action> actions;

	public Mapping()
	{
		this(new ArrayList<>());
	}

	public Mapping(List<Action> actions)
	{
		this(UUID.randomUUID(), actions);
	}

	public Mapping(UUID id, List<Action> actions)
	{
		this.id = id;
		this.actions = actions;
	}

	public UUID getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void addAction(Action action)
	{
		this.actions.add(action);
	}

	public void addUniqueAction(Action action)
	{
		if(!actions.contains(action))
		{
			actions.add(action);
		}
	}

	public void removeAction(Action action)
	{
		this.actions.remove(action);
	}

	public List<Action> getActions()
	{
		return actions;
	}

	/*
	Get Actions
	 */

	private transient Map<Integer, Action> midiCache = new HashMap<>();

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
		return actions.parallelStream()
				.filter(a -> a.getActionType().equals(type))
				.collect(Collectors.toList());
	}

	public Action getFirstActionOrCreateForType(String type)
	{
		final List<Action> actionList = Mapping.getCurrentMapping().getActionsForType(type);
		if(actionList.isEmpty())
		{
			final Action action = new Action(type);
			Mapping.getCurrentMapping().addAction(action);
			return action;
		}
		return actionList.get(0);
	}

	public Optional<MidiKey> getFirstMidiKeyForAction(Action action)
	{
		if(!action.getKeys().isEmpty())
		{
			return action.getKeys().parallelStream().filter(key -> key instanceof MidiKey).map(key -> (MidiKey) key).findAny();
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
			return action.getKeys().parallelStream().filter(key -> key instanceof KeyboardKey).map(key -> (KeyboardKey) key).findAny();
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

	public Optional<Action> getActionForTypeWithPayload(String type, Map<String, String> payload)
	{
		return actions.parallelStream()
				.filter(action -> action.getActionType().equals(type))
				.filter(action -> action.getPayload().entrySet().containsAll(payload.entrySet()))
				.findFirst();
	}

	@Override
	public String toString()
	{
		return "Mapping{" +
				"actions=" + actions +
				'}';
	}
}
