package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.mapping.action.TestAction;
import de.thecodelabs.midi.mapping.input.KeyboardInputKey;
import de.thecodelabs.midi.mapping.input.MidiInputKey;
import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MappingTest
{
	@Test
	void testSerialize()
	{
		final MappingRegistry registry = new MappingRegistry();
		registry.registerAction(TestAction.class);
		registry.registerInputKey(MidiInputKey.class)
				.registerInputKey(KeyboardInputKey.class);

		final MappingSerializer serializer = registry.build();

		final Mapping mapping = new Mapping();
		mapping.addAction(new MidiInputKey((byte) 34), new TestAction("Demo"));
		mapping.addAction(new KeyboardInputKey(KeyCode.E, "E"), new TestAction("Demo2"));

		final String serialize = serializer.serialize(mapping);
		final Mapping deserialize = serializer.deserialize(serialize);

		assertThat(deserialize).isEqualTo(mapping);
	}
}
