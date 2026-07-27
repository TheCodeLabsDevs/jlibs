package de.thecodelabs.midi.mapping.feedback;

import de.thecodelabs.midi.mapping.input.InputKey;

public interface FeedbackValueWriter<I extends InputKey, V extends FeedbackValue>
{
	void write(I key, V value);
}
