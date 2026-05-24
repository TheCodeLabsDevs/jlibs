package de.thecodelabs.midi.midi.feedback;

import de.thecodelabs.midi.feedback.FeedbackType;
import de.thecodelabs.midi.feedback.FeedbackValue;
import de.thecodelabs.midi.mapping.input.MidiInputKey;

import java.util.Optional;

public interface MidiFeedbackTranscript
{
	void sendFeedback(MidiInputKey midiKey, FeedbackType feedbackType);

	void clearFeedback();

	FeedbackValue[] getFeedbackValues();

	Optional<FeedbackValue> getFeedbackValueOfByte(byte value);
}
