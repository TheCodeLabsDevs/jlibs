package de.tobias.midi.event;

import de.tobias.midi.mapping.KeyType;

public class KeyEvent
{
	private final KeyType type;
	private final KeyEventType keyEventType;
	private final int keyValue;

	public KeyEvent(KeyType type, KeyEventType keyEventType, int keyValue)
	{
		this.type = type;
		this.keyEventType = keyEventType;
		this.keyValue = keyValue;
	}

	public KeyType getType()
	{
		return type;
	}

	public KeyEventType getKeyEventType()
	{
		return keyEventType;
	}

	public int getKeyValue()
	{
		return keyValue;
	}
}
