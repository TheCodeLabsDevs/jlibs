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

	private FeedbackType lastValue;

	@Override
	public FeedbackType handle(KeyEvent keyEvent, Action action)
	{
		System.out.println(action.getPayload());

		if(lastValue != null)
		{
			if(lastValue == FeedbackType.DEFAULT)
			{
				lastValue = FeedbackType.EVENT;
				return FeedbackType.EVENT;
			}
			else if(lastValue == FeedbackType.EVENT)
			{
				lastValue = FeedbackType.WARNING;
				return FeedbackType.WARNING;
			}
			else
			{
				lastValue = FeedbackType.DEFAULT;
				return FeedbackType.DEFAULT;
			}
		}
		lastValue = FeedbackType.DEFAULT;
		return FeedbackType.DEFAULT;
	}

	@Override
	public FeedbackType getCurrentFeedbackType(Action action)
	{
		return FeedbackType.DEFAULT;
	}
}
