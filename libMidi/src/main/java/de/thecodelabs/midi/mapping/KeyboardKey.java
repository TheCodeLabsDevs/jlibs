package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.feedback.Feedback;
import de.thecodelabs.midi.feedback.FeedbackType;
import javafx.scene.input.KeyCode;

import java.util.Objects;

public class KeyboardKey extends Key
{
	private KeyCode code;
	private String key;

	public KeyboardKey()
	{
	}

	public KeyboardKey(KeyCode code, String key)
	{
		this.code = code;
		this.key = key;
	}

	@Override
	public KeyType getType()
	{
		return KeyType.KEYBOARD;
	}

	public KeyCode getCode()
	{
		return code;
	}

	public void setCode(KeyCode code)
	{
		this.code = code;
	}

	public String getKey()
	{
		return key;
	}

	public void setKey(String key)
	{
		this.key = key;
	}

	@Override
	public Feedback getFeedbackForType(FeedbackType feedbackType)
	{
		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(!(o instanceof KeyboardKey)) return false;
		KeyboardKey that = (KeyboardKey) o;
		return code == that.code &&
				Objects.equals(key, that.key);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(code, key);
	}

	@Override
	public String toString()
	{
		return "KeyboardKey{" +
				"code=" + code +
				", key='" + key + '\'' +
				'}';
	}
}
