package de.thecodelabs.midi.midi.message;

import javax.sound.midi.ShortMessage;

public enum MidiMessageType
{
	NOTE_ON(ShortMessage.NOTE_ON),
	NOTE_OFF(ShortMessage.NOTE_OFF),
	CONTROL_CHANGE(ShortMessage.CONTROL_CHANGE),
	SYSTEM_EXCLUSIVE(-1),
	UNKNOWN(-1);

	private final int midiValue;

	MidiMessageType(int midiValue)
	{
		this.midiValue = midiValue;
	}

	public int getMidiValue()
	{
		return midiValue;
	}
}
