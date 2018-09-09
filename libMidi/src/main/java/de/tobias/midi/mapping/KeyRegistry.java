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

			instance.register("MidiKey", MidiKey.class);
			instance.register("KeyboardKey", KeyboardKey.class);
		}
		return instance;
	}

	private Map<String, Class<? extends Key>> types = new HashMap<>();

	public Class<? extends Key> getType(String type)
	{
		return types.get(type);
	}

	public String getIdentifier(Key obj)
	{
		return getIdentifier(obj.getClass());
	}

	public String getIdentifier(Class<? extends Key> clazz)
	{
		for(String type : types.keySet())
		{
			if(types.get(type).equals(clazz))
			{
				return type;
			}
		}
		return null;
	}

	public void register(String type, Class<? extends Key> clazz)
	{
		this.types.put(type, clazz);
	}
}
