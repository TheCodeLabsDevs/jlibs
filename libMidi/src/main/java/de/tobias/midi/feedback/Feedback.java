package de.tobias.midi.feedback;

public class Feedback
{
	private byte channel;
	private byte value;

	public Feedback()
	{
	}

	public Feedback(byte channel, byte value)
	{
		this.channel = channel;
		this.value = value;
	}

	public byte getChannel()
	{
		return channel;
	}

	public void setChannel(byte channel)
	{
		this.channel = channel;
	}

	public byte getValue()
	{
		return value;
	}

	public void setValue(byte value)
	{
		this.value = value;
	}

	@Override
	public String toString()
	{
		return "Feedback{" +
				"channel=" + channel +
				", value=" + value +
				'}';
	}
}
