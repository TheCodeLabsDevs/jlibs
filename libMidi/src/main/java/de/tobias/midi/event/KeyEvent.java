package de.tobias.midi.event;

public class KeyEvent
{
	private final KeyEventType keyEventType;
	private final int keyValue;

	public KeyEvent(KeyEventType keyEventType, int keyValue)
	{
		this.keyEventType = keyEventType;
		this.keyValue = keyValue;
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
