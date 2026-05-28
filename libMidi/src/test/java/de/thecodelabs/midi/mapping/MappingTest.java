package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.mapping.action.TestAction;
import de.thecodelabs.midi.mapping.action.TestActionHandler;
import de.thecodelabs.midi.mapping.feedback.DemoFeedbackState;
import de.thecodelabs.midi.mapping.feedback.DemoFeedbackValue;
import de.thecodelabs.midi.mapping.feedback.DemoFeedbackValueWriter;
import de.thecodelabs.midi.mapping.input.KeyboardInputKey;
import de.thecodelabs.midi.mapping.input.MidiInputKey;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.CloseException;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MappingTest
{
	@Test
	void testSerialize()
	{
		try (final Midi midi = new Midi())
		{
			final MappingRegistry registry = new MappingRegistry();
			registry.registerAction(TestAction.class, new TestActionHandler());
			registry.registerInputKey(MidiInputKey.class)
					.registerInputKey(KeyboardInputKey.class);
			registry.registerFeedbackState(DemoFeedbackState.class)
					.registerFeedbackValue(MidiInputKey.class, DemoFeedbackValue.class, new DemoFeedbackValueWriter(midi));

			final MappingSerializer serializer = registry.build();

			final Mapping mapping = new Mapping();
			mapping.addInputKeyWithAction(new MidiInputKey((byte) 34, Map.of(DemoFeedbackState.PLAYING, new DemoFeedbackValue(3))), new TestAction("Demo"));
			mapping.addInputKeyWithAction(new KeyboardInputKey(KeyCode.E, "E"), new TestAction("Demo2"));

			final String serialize = serializer.serialize(mapping);
			final Mapping deserialize = serializer.deserialize(serialize);

			assertThat(deserialize).isEqualTo(mapping);
		}
		catch(CloseException e)
		{
			throw new RuntimeException(e);
		}
	}
}
