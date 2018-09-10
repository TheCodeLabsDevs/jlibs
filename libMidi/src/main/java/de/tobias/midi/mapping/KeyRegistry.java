package de.tobias.midi.mapping;

import java.util.HashMap;
import java.util.Map;

public class KeyRegistry
{
	private static KeyRegistry instance;

	public static KeyRegistry getInstance()
	{
		if(instance == null)
		{
			instance = new KeyRegistry();

			instance.register(KeyTypes.MIDI, MidiKey.class);
			instance.register(KeyTypes.KEYBOARD, KeyboardKey.class);
		}
		return instance;
	}

	private Map<KeyTypes, Class<? extends Key>> types = new HashMap<>();

	public Class<? extends Key> getType(KeyTypes type)
	{
		return types.get(type);
	}

	public KeyTypes getIdentifier(Key obj)
	{
		return getIdentifier(obj.getClass());
	}

	public KeyTypes getIdentifier(Class<? extends Key> clazz)
	{
		for(KeyTypes type : types.keySet())
		{
			if(types.get(type).equals(clazz))
			{
				return type;
			}
		}
		return null;
	}

	public void register(KeyTypes type, Class<? extends Key> clazz)
	{
		this.types.put(type, clazz);
	}
}
