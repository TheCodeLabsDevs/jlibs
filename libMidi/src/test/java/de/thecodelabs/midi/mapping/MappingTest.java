package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.mapping.action.TestAction;
import de.thecodelabs.midi.mapping.action.TestActionHandler;
import de.thecodelabs.midi.mapping.feedback.DemoFeedbackState;
import de.thecodelabs.midi.mapping.feedback.DemoFeedbackValue;
import de.thecodelabs.midi.mapping.input.KeyboardInputKey;
import de.thecodelabs.midi.mapping.input.MidiInputKey;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MappingTest
{
	@Test
	void testSerialize()
	{
		final MappingRegistry registry = new MappingRegistry();
		registry.registerAction(TestAction.class, new TestActionHandler());
		registry.registerInputKey(MidiInputKey.class)
				.registerInputKey(KeyboardInputKey.class);
		registry.registerFeedbackState(DemoFeedbackState.class)
				.registerFeedbackValue(DemoFeedbackValue.class);

		final MappingSerializer serializer = registry.build();

		final Mapping mapping = new Mapping();
		mapping.addInputKeyWithAction(new MidiInputKey((byte) 34, Map.of(DemoFeedbackState.PLAYING, new DemoFeedbackValue("playing"))), new TestAction("Demo"));
		mapping.addInputKeyWithAction(new KeyboardInputKey(KeyCode.E, "E"), new TestAction("Demo2"));

		final String serialize = serializer.serialize(mapping);
		final Mapping deserialize = serializer.deserialize(serialize);

		assertThat(deserialize).isEqualTo(mapping);
	}
}
