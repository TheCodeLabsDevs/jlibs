package de.thecodelabs.midi;

import javax.sound.midi.ShortMessage;

public enum MidiCommandType
{
	NOTE_ON,
	NOTE_OFF,
	CONTROL_CHANGE,
	SYSTEM_EXCLUSIVE;

	public int getMidiValue()
	{
		switch(this)
		{
			case NOTE_ON:
				return ShortMessage.NOTE_ON;
			case NOTE_OFF:
				return ShortMessage.NOTE_OFF;
			case CONTROL_CHANGE:
				return ShortMessage.CONTROL_CHANGE;
			case SYSTEM_EXCLUSIVE:
				break;
		}
		return -1;
	}
}
