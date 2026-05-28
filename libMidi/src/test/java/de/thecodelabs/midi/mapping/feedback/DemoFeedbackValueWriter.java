package de.thecodelabs.midi.mapping.feedback;

import de.thecodelabs.midi.mapping.input.MidiInputKey;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.message.MidiMessage;
import de.thecodelabs.midi.midi.message.MidiMessageType;

public class DemoFeedbackValueWriter implements FeedbackValueWriter<MidiInputKey, DemoFeedbackValue>
{
	private final Midi midi;

	public DemoFeedbackValueWriter(Midi midi)
	{
		this.midi = midi;
	}

	@Override
	public void write(MidiInputKey key, DemoFeedbackValue value)
	{
		midi.getDevice().sendMidiMessage(new MidiMessage(MidiMessageType.NOTE_ON, (byte) 0, key.value(), (byte) value.value()));
	}
}
