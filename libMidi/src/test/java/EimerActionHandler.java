import de.thecodelabs.midi.action.Action;
import de.thecodelabs.midi.action.ActionHandler;
import de.thecodelabs.midi.event.KeyEvent;
import de.thecodelabs.midi.feedback.FeedbackType;

public class EimerActionHandler extends ActionHandler
{
	@Override
	public String actionType()
	{
		return "EIMER";
	}

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		System.out.println(action.getPayload());
		return FeedbackType.DEFAULT;
	}

	@Override
	public FeedbackType getCurrentFeedbackType(Action action)
	{
		return null;
	}
}
