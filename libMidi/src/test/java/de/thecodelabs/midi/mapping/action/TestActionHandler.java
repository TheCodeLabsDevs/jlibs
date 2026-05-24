package de.thecodelabs.midi.mapping.action;

import de.thecodelabs.midi.event.KeyInputEvent;

public class TestActionHandler implements ActionHandler
{
	@Override
	public void handleAction(KeyInputEvent event, Action action)
	{
		System.out.println("Handle");
	}
}
