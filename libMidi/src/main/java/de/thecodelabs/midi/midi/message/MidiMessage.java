package de.thecodelabs.midi.midi.message;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.util.Arrays;

public class MidiMessage
{
	private boolean consumed;

	private final MidiMessageType messageType;
	private final byte channel;
	private final byte[] payload;

	public MidiMessage(MidiMessageType messageType, byte data1, byte data2)
	{
		this(messageType, (byte) 0, new byte[]{data1, data2});
	}

	public MidiMessage(MidiMessageType messageType, byte channel, byte[] payload)
	{
		this.messageType = messageType;
		this.channel = channel;
		this.payload = payload;
	}

	public MidiMessage(byte[] data)
	{
		if(data.length == 0)
		{
			this.messageType = MidiMessageType.UNKNOWN;
			this.channel = 0;
			this.payload = new byte[0];
			return;
		}

		int statusByte = Byte.toUnsignedInt(data[0]);
		int command = statusByte & 0xF0;
		this.channel = (byte) (statusByte & 0x0F);

		switch(command)
		{
			case ShortMessage.NOTE_ON:
				this.messageType = MidiMessageType.NOTE_ON;
				break;
			case ShortMessage.NOTE_OFF:
				this.messageType = MidiMessageType.NOTE_OFF;
				break;
			case ShortMessage.CONTROL_CHANGE:
				this.messageType = MidiMessageType.CONTROL_CHANGE;
				break;
			case SysexMessage.SYSTEM_EXCLUSIVE:
				this.messageType = MidiMessageType.SYSTEM_EXCLUSIVE;
				break;
			default:
				this.messageType = MidiMessageType.UNKNOWN;
				break;
		}

		this.payload = new byte[data.length - 1];
		System.arraycopy(data, 1, payload, 0, data.length - 1);
	}

	public MidiMessage(javax.sound.midi.MidiMessage message)
	{
		this(message.getMessage());
	}

	public MidiMessage(MidiMessageType command, byte channel, byte data1, byte data2)
	{
		this(command, channel, new byte[]{data1, data2});
	}

	public void consume()
	{
		consumed = true;
	}

	public boolean isConsumed()
	{
		return consumed;
	}

	public MidiMessageType getMessageType()
	{
		return messageType;
	}

	public byte getChannel()
	{
		return channel;
	}

	public byte[] getPayload()
	{
		return payload.clone();
	}

	@Override
	public boolean equals(Object o)
	{
		if(o == null || getClass() != o.getClass()) return false;
		MidiMessage that = (MidiMessage) o;
		return channel == that.channel
		       && messageType == that.messageType
		       && Arrays.equals(payload, that.payload);
	}

	@Override
	public int hashCode()
	{
		int result = messageType != null ? messageType.hashCode() : 0;
		result = 31 * result + channel;
		result = 31 * result + Arrays.hashCode(payload);
		return result;
	}

	@Override
	public String toString()
	{
		return "MidiMessage{" +
		       "messageType=" + messageType +
		       ", payload=" + Arrays.toString(payload) +
		       '}';
	}
}
