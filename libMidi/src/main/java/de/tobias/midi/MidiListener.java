package de.tobias.midi;

public interface MidiListener
{
	void onMidiMessage(MidiCommand midiEvent);
}
