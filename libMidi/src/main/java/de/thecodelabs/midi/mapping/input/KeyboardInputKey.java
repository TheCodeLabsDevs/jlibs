package de.thecodelabs.midi.mapping.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import javafx.scene.input.KeyCode;

@JsonTypeName("keyboard")
public record KeyboardInputKey(KeyCode code, String key) implements InputKey
{
	@JsonCreator
	public KeyboardInputKey(@JsonProperty("code") KeyCode code, @JsonProperty("key") String key)
	{
		this.code = code;
		this.key = key;
	}

	@Override
	public String toString()
	{
		return "KeyboardInputKey{" +
		       "code=" + code +
		       ", key='" + key + '\'' +
		       '}';
	}
}
