package de.tobias.midi;

import java.util.Objects;

public class Key
{
	private int value;

	public Key()
	{
	}

	public Key(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
	{
		this.value = value;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof Key)) return false;
		Key key = (Key) o;
		return value == key.value;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(value);
	}
}
