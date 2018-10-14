package de.thecodelabs.midi.action;

import java.util.HashMap;
import java.util.Map;

public class ActionRegistry
{
	private static Map<String, ActionHandler> actionHandlerMap;

	static {
		actionHandlerMap = new HashMap<>();
	}

	public static void registerActionHandler(ActionHandler actionHandler) {
		if (actionHandlerMap.containsKey(actionHandler.actionType()))
		{
			throw new RuntimeException("ActionHandler " + actionHandler.actionType() + " already registered");
		}
		actionHandlerMap.put(actionHandler.actionType(), actionHandler);
	}

	public static ActionHandler getActionHandler(String type) {
		if (!actionHandlerMap.containsKey(type)) {
			throw new RuntimeException("Action " + type + " not exists");
		}
		return actionHandlerMap.get(type);
	}
}
