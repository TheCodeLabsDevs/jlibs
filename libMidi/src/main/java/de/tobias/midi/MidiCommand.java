package de.tobias.midi;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.util.Arrays;

public class MidiCommand
{
	private boolean consumed;

	private final MidiCommandType midiCommand;
	private final byte channel;
	private final byte[] payload;

	public MidiCommand(MidiCommandType midiCommand, byte data1, byte data2)
	{
		this(midiCommand, (byte) 0, new byte[]{data1, data2});
	}

	public MidiCommand(MidiCommandType midiCommand, byte channel, byte[] payload)
	{
		this.midiCommand = midiCommand;
		this.channel = channel;
		this.payload = payload;
	}

	public MidiCommand(byte[] data)
	{
		int command = Byte.toUnsignedInt(data[0]);
		switch(command)
		{
			case ShortMessage.NOTE_ON:
				this.midiCommand = MidiCommandType.NOTE_ON;
				break;
			case ShortMessage.NOTE_OFF:
				this.midiCommand = MidiCommandType.NOTE_OFF;
				break;
			case ShortMessage.CONTROL_CHANGE:
				this.midiCommand = MidiCommandType.CONTROL_CHANGE;
				break;
			case SysexMessage.SYSTEM_EXCLUSIVE:
				this.midiCommand = MidiCommandType.SYSTEM_EXCLUSIVE;
				break;
			default:
				this.midiCommand = null;
				break;
		}

		byte[] payload = new byte[data.length - 1];
		System.arraycopy(data, 1, payload, 0, data.length - 1);
		this.payload = payload;

		this.channel = 0;
	}

	public MidiCommand(javax.sound.midi.MidiMessage message)
	{
		this(message.getMessage());
	}

	public MidiCommand(MidiCommandType command, byte channel, byte data1, byte data2)
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

	public MidiCommandType getMidiCommand()
	{
		return midiCommand;
	}

	public byte getChannel()
	{
		return channel;
	}

	public byte[] getPayload()
	{
		return payload;
	}

	@Override
	public String toString()
	{
		return "MidiEvent{" +
				"midiCommand=" + midiCommand +
				", payload=" + Arrays.toString(payload) +
				'}';
	}
}
