package de.thecodelabs.midi.mapping.listener;

import de.thecodelabs.midi.event.KeyInputEvent;
import de.thecodelabs.midi.event.KeyInputType;
import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.action.ActionHandler;
import de.thecodelabs.midi.mapping.action.ActionHandlerResolver;
import de.thecodelabs.midi.mapping.feedback.FeedbackState;
import de.thecodelabs.midi.mapping.feedback.FeedbackValue;
import de.thecodelabs.midi.mapping.feedback.FeedbackValueWriter;
import de.thecodelabs.midi.mapping.feedback.FeedbackValueWriterResolver;
import de.thecodelabs.midi.mapping.input.MidiInputKey;
import de.thecodelabs.midi.midi.message.MidiMessage;
import de.thecodelabs.midi.midi.message.MidiMessageListener;
import de.thecodelabs.midi.midi.message.MidiMessageType;

import java.util.Optional;

public class MidiMappingListener implements MidiMessageListener
{
	private final Mapping mapping;
	private final ActionHandlerResolver actionHandlerResolver;
	private final FeedbackValueWriterResolver feedbackValueWriterResolver;

	public MidiMappingListener(Mapping mapping, ActionHandlerResolver actionHandlerResolver, FeedbackValueWriterResolver feedbackValueWriterResolver)
	{
		this.mapping = mapping;
		this.actionHandlerResolver = actionHandlerResolver;
		this.feedbackValueWriterResolver = feedbackValueWriterResolver;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public void onMidiMessage(MidiMessage midiEvent)
	{
		final byte[] payload = midiEvent.getPayload();
		if(midiEvent.getMessageType() != null
		   && midiEvent.getMessageType() != MidiMessageType.SYSTEM_EXCLUSIVE
		   && payload.length >= 2)
		{
			final int key = payload[0];
			final int velocity = payload[1];

			final Optional<MidiInputKey> midiKeyOptional = mapping.getInputKeyForPredicate(inputKey ->
					inputKey instanceof MidiInputKey midiKey
					&& midiKey.value() == key);
			if(midiKeyOptional.isEmpty())
			{
				return;
			}

			final MidiInputKey inputKey = midiKeyOptional.get();
			final Action action = mapping.getAction(inputKey);
			final ActionHandler actionHandler = actionHandlerResolver.resolve(action);
			if(actionHandler == null)
			{
				return;
			}

			final KeyInputType type = velocity > 0 ? KeyInputType.DOWN : KeyInputType.UP;
			final KeyInputEvent keyEvent = new KeyInputEvent(inputKey, type);

			final FeedbackState feedbackState = actionHandler.handleAction(keyEvent, action);
			final FeedbackValue feedbackValue = inputKey.feedbackValues().get(feedbackState);
			if(feedbackValue != null)
			{
				final FeedbackValueWriter feedbackValueWriter = feedbackValueWriterResolver.resolve(inputKey, feedbackValue);
				feedbackValueWriter.write(inputKey, feedbackValue);
			}
		}
	}
}
