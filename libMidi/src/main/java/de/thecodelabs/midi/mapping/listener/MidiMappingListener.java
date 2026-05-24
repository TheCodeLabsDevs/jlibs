package de.thecodelabs.midi.mapping.listener;

import de.thecodelabs.midi.event.KeyInputEvent;
import de.thecodelabs.midi.event.KeyInputType;
import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.action.ActionHandler;
import de.thecodelabs.midi.mapping.action.ActionHandlerResolver;
import de.thecodelabs.midi.mapping.input.InputKey;
import de.thecodelabs.midi.mapping.input.MidiInputKey;
import de.thecodelabs.midi.midi.message.MidiListener;
import de.thecodelabs.midi.midi.message.MidiMessage;
import de.thecodelabs.midi.midi.message.MidiMessageType;

import java.util.Optional;

public class MidiMappingListener implements MidiListener
{
	private final Mapping mapping;
	private final ActionHandlerResolver actionHandlerResolver;

	public MidiMappingListener(Mapping mapping, ActionHandlerResolver actionHandlerResolver)
	{
		this.mapping = mapping;
		this.actionHandlerResolver = actionHandlerResolver;
	}

	@Override
	public void onMidiMessage(MidiMessage midiEvent)
	{
		final byte[] payload = midiEvent.getPayload();
		if(midiEvent.getMessageType() != null
		   && midiEvent.getMessageType() != MidiMessageType.SYSTEM_EXCLUSIVE
		   && !midiEvent.isConsumed()
		   && payload.length >= 2)
		{
			final int key = payload[0];
			final int velocity = payload[1];

			final Optional<InputKey> midiKeyOptional = mapping.getInputKeyForPredicate(inputKey ->
					inputKey instanceof MidiInputKey midiKey
					&& midiKey.getValue() == key);
			if(midiKeyOptional.isEmpty())
			{
				return;
			}

			final Action action = mapping.getAction(midiKeyOptional.get());
			final ActionHandler actionHandler = actionHandlerResolver.resolve(action);

			final KeyInputType type = velocity > 0 ? KeyInputType.DOWN : KeyInputType.UP;
			final KeyInputEvent keyEvent = new KeyInputEvent(midiKeyOptional.get(), type);
			actionHandler.handleAction(keyEvent, action);
		}
	}
}
