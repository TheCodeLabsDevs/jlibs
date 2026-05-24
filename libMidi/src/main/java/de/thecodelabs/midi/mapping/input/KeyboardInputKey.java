package de.thecodelabs.midi.mapping.input;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.scene.input.KeyCode;

import java.util.Objects;

public record KeyboardInputKey(KeyCode code, String key) implements InputKey
{
	@JsonCreator
	public KeyboardInputKey(@JsonProperty("code") KeyCode code, @JsonProperty("key") String key)
	{
		this.code = code;
		this.key = key;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null || getClass() != o.getClass()) return false;
		KeyboardInputKey that = (KeyboardInputKey) o;
		return code == that.code && Objects.equals(key, that.key);
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
