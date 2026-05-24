package de.thecodelabs.midi.mapping.feedback;

public class MidiFeedback implements MidiFeedbackValue
{
	private byte channel;
	private byte value;

	public MidiFeedback()
	{
	}

	public MidiFeedback(byte channel, byte value)
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
		return "MidiFeedback{" +
		       "channel=" + channel +
		       ", value=" + value +
		       '}';
	}
}
