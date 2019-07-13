package de.thecodelabs.midi.feedback;

public class Feedback implements FeedbackValue
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

	public static Feedback copy(Feedback feedback)
	{
		if(feedback == null)
		{
			return null;
		}

		Feedback copy = new Feedback();
		copy.value = feedback.value;
		copy.channel = feedback.channel;
		return copy;
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
