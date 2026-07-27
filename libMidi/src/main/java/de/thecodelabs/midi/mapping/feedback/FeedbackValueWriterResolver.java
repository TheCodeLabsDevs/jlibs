package de.thecodelabs.midi.mapping.feedback;

import de.thecodelabs.midi.mapping.input.InputKey;

public interface FeedbackValueWriterResolver
{
	FeedbackValueWriter<?, ?> resolve(InputKey key, FeedbackValue value);
}
