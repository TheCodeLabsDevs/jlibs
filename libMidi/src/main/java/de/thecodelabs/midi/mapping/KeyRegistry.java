package de.thecodelabs.midi.mapping;

import java.util.EnumMap;
import java.util.Map;

public class KeyRegistry
{
	private static KeyRegistry instance;

	public static KeyRegistry getInstance()
	{
		if(instance == null)
		{
			instance = new KeyRegistry();

			instance.register(KeyType.MIDI, MidiKey.class);
			instance.register(KeyType.KEYBOARD, KeyboardKey.class);
		}
		return instance;
	}

	private Map<KeyType, Class<? extends Key>> types = new EnumMap<>(KeyType.class);

	public Class<? extends Key> getType(KeyType type)
	{
		return types.get(type);
	}

	public KeyType getIdentifier(Key obj)
	{
		return getIdentifier(obj.getClass());
	}

	public KeyType getIdentifier(Class<? extends Key> clazz)
	{
		for(Map.Entry<KeyType, Class<? extends Key>> type : types.entrySet())
		{
			if(type.getValue().equals(clazz))
			{
				return type.getKey();
			}
		}
		return null;
	}

	public void register(KeyType type, Class<? extends Key> clazz)
	{
		this.types.put(type, clazz);
	}
}
