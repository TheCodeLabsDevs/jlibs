package de.tobias.midi.event;

import de.tobias.midi.Key;

import java.util.ArrayList;
import java.util.List;

public abstract class Action
{
	private List<Key> keys;

	public Action()
	{
		keys = new ArrayList<>();
	}

	public abstract void handle(KeyEvent keyEvent);
}
