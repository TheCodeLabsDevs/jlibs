package de.tobias.midi.event;

import java.util.LinkedList;
import java.util.List;

public class KeyEventDispatcher
{
	private static List<KeyEventHandler> keyEventHandlerList;

	static {
		keyEventHandlerList = new LinkedList<>();
	}

	public static void registerKeyEventHandler(KeyEventHandler keyEventHandler) {
		KeyEventDispatcher.keyEventHandlerList.add(keyEventHandler);
	}

	public static void dispatchEvent(KeyEvent keyEvent) {
		for(KeyEventHandler keyEventHandler : keyEventHandlerList)
		{
			try {
				keyEventHandler.handleMidiKeyEvent(keyEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
