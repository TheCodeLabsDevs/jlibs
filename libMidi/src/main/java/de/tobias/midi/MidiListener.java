package de.tobias.midi;

import javax.sound.midi.MidiMessage;

public interface MidiListener
{
	void onMidiMessage(MidiMessage midiMessage);
}
