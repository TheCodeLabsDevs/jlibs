import de.tobias.midi.action.Action;
import de.tobias.midi.action.ActionHandler;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.feedback.FeedbackType;

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
}
