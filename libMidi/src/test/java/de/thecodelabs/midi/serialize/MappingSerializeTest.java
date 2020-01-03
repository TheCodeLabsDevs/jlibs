package de.thecodelabs.midi.serialize;

import de.thecodelabs.midi.Mapping;
import de.thecodelabs.midi.action.Action;
import de.thecodelabs.midi.feedback.Feedback;
import de.thecodelabs.midi.mapping.KeyboardKey;
import de.thecodelabs.midi.mapping.MidiKey;
import javafx.scene.input.KeyCode;
import org.assertj.core.data.MapEntry;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class MappingSerializeTest
{
	@Test
	public void loadTest()
	{
		Mapping mapping = MappingSerializer.load(getClass().getClassLoader().getResourceAsStream("midi1.json"));

		assertThat(mapping).isNotNull();
		assertThat(mapping.getActions()).hasSize(1);

		final Action action = mapping.getActions().get(0);
		assertThat(action).hasFieldOrPropertyWithValue("actionType", "EIMER");
		assertThat(action.getPayload()).containsExactly(MapEntry.entry("player", "eimer"));

		assertThat(action.getKeys()).hasSize(1).hasOnlyElementsOfType(MidiKey.class);

		final MidiKey key = (MidiKey) action.getKeys().get(0);
		assertThat(key)
				.hasFieldOrPropertyWithValue("value", (byte) 43);

		assertThat(key.getDefaultFeedback())
				.hasFieldOrPropertyWithValue("channel", (byte) 0)
				.hasFieldOrPropertyWithValue("value", (byte) 45);

		assertThat(key.getEventFeedback())
				.hasFieldOrPropertyWithValue("channel", (byte) 1)
				.hasFieldOrPropertyWithValue("value", (byte) 46);

	}

	@Test
	public void saveTest() throws IOException
	{
		Mapping mapping = new Mapping();

		Action action = new Action("SAVE");
		action.addKey(new KeyboardKey(KeyCode.K, "K"));
		action.addKey(new MidiKey((byte) 46));
		action.addKey(new MidiKey((byte) 42, new Feedback((byte) 0, (byte) 32), new Feedback((byte) 1, (byte) 34)));
		action.addPayloadEntry("data", "null");

		mapping.addAction(action);

		StringBuffer buffer = new StringBuffer();
		MappingSerializer.save(mapping, buffer);

		final String result = buffer.toString();

		assertThat(result)
				.contains("SAVE")
				.contains("K")
				.contains("46")
				.contains("42")
				.contains("0", "32")
				.contains("1", "34")
				.contains("data", "null");
	}
}
