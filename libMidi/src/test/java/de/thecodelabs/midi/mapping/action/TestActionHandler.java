package de.thecodelabs.midi.mapping.action;

import de.thecodelabs.midi.event.KeyInputEvent;
import de.thecodelabs.midi.event.KeyInputType;
import de.thecodelabs.midi.mapping.feedback.DemoFeedbackState;
import de.thecodelabs.midi.mapping.feedback.FeedbackState;

public class TestActionHandler implements ActionHandler
{
	@Override
	public FeedbackState handleAction(KeyInputEvent event, Action action)
	{
		System.out.println("Handle");
		if(event.keyInputType() == KeyInputType.DOWN)
		{
			return DemoFeedbackState.PLAYING;
		}
		else
		{
			return DemoFeedbackState.NORMAL;
		}
	}

	@Override
	public FeedbackState getCurrentState(Action action)
	{
		return DemoFeedbackState.NORMAL;
	}
}
