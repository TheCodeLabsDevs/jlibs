package de.thecodelabs.midi.midi.feedback;

import de.thecodelabs.midi.feedback.FeedbackType;
import de.thecodelabs.midi.feedback.FeedbackValue;
import de.thecodelabs.midi.mapping.MidiKey;

public interface MidiFeedbackTranscript
{
	void sendFeedback(MidiKey midiKey, FeedbackType feedbackType);

	void clearFeedback();

	FeedbackValue[] getFeedbackValues();
}
