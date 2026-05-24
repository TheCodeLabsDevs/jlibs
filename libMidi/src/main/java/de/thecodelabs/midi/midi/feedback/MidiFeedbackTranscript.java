package de.thecodelabs.midi.midi.feedback;

import de.thecodelabs.midi.mapping.feedback.FeedbackState;
import de.thecodelabs.midi.mapping.feedback.MidiFeedbackValue;
import de.thecodelabs.midi.mapping.input.MidiInputKey;

import java.util.Optional;

public interface MidiFeedbackTranscript
{
	void sendFeedback(MidiInputKey midiKey, FeedbackState feedbackType);

	void clearFeedback();

	MidiFeedbackValue[] getFeedbackValues();

	Optional<MidiFeedbackValue> getFeedbackValueOfByte(byte value);
}
