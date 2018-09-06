package de.tobias.midi;

import javax.sound.midi.MidiMessage;

public class MidiEvent
{
	private boolean consumed;
	private final MidiMessage midiMessage;

	public MidiEvent(MidiMessage midiMessage)
	{
		this.midiMessage = midiMessage;
	}

	public void consume()
	{
		consumed = true;
	}

	public boolean isConsumed()
	{
		return consumed;
	}

	public MidiMessage getMidiMessage()
	{
		return midiMessage;
	}
}
