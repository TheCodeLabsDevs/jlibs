package de.tobias.midi;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.SysexMessage;
import java.util.Arrays;

public class MidiCommand
{
	private boolean consumed;

	private final MidiCommandType midiCommand;
	private final byte[] payload;

	public MidiCommand(MidiCommandType midiCommand, byte data1, byte data2)
	{
		this(midiCommand, new byte[]{data1, data2});
	}

	public MidiCommand(MidiCommandType midiCommand, byte[] payload)
	{
		this.midiCommand = midiCommand;
		this.payload = payload;
	}

	public MidiCommand(javax.sound.midi.MidiMessage message)
	{
		if(message instanceof ShortMessage)
		{
			int command = Byte.toUnsignedInt(message.getMessage()[0]);
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
				default:
					this.midiCommand = null;
					break;
			}
		}
		else if(message instanceof SysexMessage)
		{
			this.midiCommand = MidiCommandType.SYSTEM_EXCLUSIVE;
		}
		else
		{
			this.midiCommand = null;
		}

		byte[] data = new byte[message.getLength() - 1];
		System.arraycopy(message.getMessage(), 1, data, 0, message.getLength() - 1);
		this.payload = data;
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
