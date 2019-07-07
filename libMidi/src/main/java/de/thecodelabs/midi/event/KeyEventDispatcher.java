package de.thecodelabs.midi.event;

import de.thecodelabs.utils.logger.LoggerBridge;

import java.util.LinkedList;
import java.util.List;

public class KeyEventDispatcher
{
	private static List<KeyEventHandler> keyEventHandlerList;

	static
	{
		keyEventHandlerList = new LinkedList<>();
	}

	private KeyEventDispatcher()
	{
	}

	public static void registerKeyEventHandler(KeyEventHandler keyEventHandler)
	{
		KeyEventDispatcher.keyEventHandlerList.add(keyEventHandler);
	}

	public static void dispatchEvent(KeyEvent keyEvent)
	{
		for(KeyEventHandler keyEventHandler : keyEventHandlerList)
		{
			try
			{
				keyEventHandler.handleKeyEvent(keyEvent);
			}
			catch(Exception e)
			{
				LoggerBridge.error(e);
			}
		}
	}
}
