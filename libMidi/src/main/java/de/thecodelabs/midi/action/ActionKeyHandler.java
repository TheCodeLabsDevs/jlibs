package de.thecodelabs.midi.action;

import de.thecodelabs.midi.Mapping;
import de.thecodelabs.midi.event.KeyEvent;
import de.thecodelabs.midi.event.KeyEventHandler;
import de.thecodelabs.midi.feedback.FeedbackType;
import de.thecodelabs.midi.mapping.KeyType;
import de.thecodelabs.midi.mapping.MidiKey;
import de.thecodelabs.midi.midi.Midi;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;

public class ActionKeyHandler implements KeyEventHandler
{
	private static boolean runOnFxThread = false;

	public static boolean isRunOnFxThread()
	{
		return runOnFxThread;
	}

	public static void setRunOnFxThread(boolean runOnFxThread)
	{
		ActionKeyHandler.runOnFxThread = runOnFxThread;
	}

	@Override
	public void handleKeyEvent(KeyEvent keyEvent)
	{
		// move to FX Thread
		if(runOnFxThread && !Platform.isFxApplicationThread())
		{
			Platform.runLater(() -> handleKeyEvent(keyEvent));
			return;
		}

		Mapping mapping = Mapping.getCurrentMapping();
		if(mapping == null)
		{
			return;
		}

		if(keyEvent.getType() == KeyType.MIDI)
		{
			handleMidiEvent(keyEvent, mapping);
		}
		else if(keyEvent.getType() == KeyType.KEYBOARD)
		{
			handleKeyboardEvent(keyEvent, mapping);
		}
	}

	private void handleKeyboardEvent(KeyEvent keyEvent, Mapping mapping)
	{
		final Action action = mapping.getActionForKeyboardKey(KeyCode.values()[keyEvent.getKeyValue()]);
		if(action != null)
		{
			ActionRegistry.getActionHandler(action.getActionType()).handle(keyEvent, action);
		}
	}

	private void handleMidiEvent(KeyEvent keyEvent, Mapping mapping)
	{
		final Action action = mapping.getActionForMidiKey(keyEvent.getKeyValue());
		if(action == null)
		{
			return;
		}

		final ActionHandler handler = ActionRegistry.getActionHandler(action.getActionType());
		FeedbackType feedbackType = handler.handle(keyEvent, action);

		if(Midi.getInstance().getDevice().isModeSupported(Midi.Mode.OUTPUT))
		{
			for(MidiKey key : action.getKeysForType(MidiKey.class))
				Midi.getInstance().sendFeedback(key, feedbackType);
		}
	}
}
