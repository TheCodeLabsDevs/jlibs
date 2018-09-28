package de.tobias.midi;

import de.tobias.midi.device.MidiCommand;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import java.util.Arrays;

public class MidiEvent
{
	private boolean consumed;

	private final MidiCommand midiCommand;
	private final byte[] payload;

	public MidiEvent(MidiCommand midiCommand, byte data1, byte data2)
	{
		this(midiCommand, new byte[]{data1, data2});
	}

	public MidiEvent(MidiCommand midiCommand, byte[] payload)
	{
		this.midiCommand = midiCommand;
		this.payload = payload;
	}

	public MidiEvent(MidiMessage message)
	{
		int command = Byte.toUnsignedInt(message.getMessage()[0]);
		switch(command)
		{
			case ShortMessage.NOTE_ON:
				this.midiCommand = MidiCommand.NOTE_ON;
				break;
			case ShortMessage.NOTE_OFF:
				this.midiCommand = MidiCommand.NOTE_OFF;
				break;
			case ShortMessage.CONTROL_CHANGE:
				this.midiCommand = MidiCommand.CONTROL_CHANGE;
				break;
			default:
				this.midiCommand = null;
				break;
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

	public MidiCommand getMidiCommand()
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
