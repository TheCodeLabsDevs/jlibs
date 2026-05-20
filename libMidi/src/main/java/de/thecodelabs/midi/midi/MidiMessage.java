package de.thecodelabs.midi.midi;

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
				this.messageType = null;
				break;
		}

		byte[] payload = new byte[data.length - 1];
		System.arraycopy(data, 1, payload, 0, data.length - 1);
		this.payload = payload;
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
	public String toString()
	{
		return "MidiEvent{" +
		       "messageType=" + messageType +
		       ", payload=" + Arrays.toString(payload) +
		       '}';
	}
}
