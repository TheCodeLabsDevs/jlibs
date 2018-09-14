package de.tobias.midi.feedback;

public class Feedback
{
	private int channel;
	private int value;

	public Feedback()
	{
	}

	public Feedback(int channel, int value)
	{
		this.channel = channel;
		this.value = value;
	}

	public int getChannel()
	{
		return channel;
	}

	public void setChannel(int channel)
	{
		this.channel = channel;
	}

	public int getValue()
	{
		return value;
	}

	public void setValue(int value)
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
