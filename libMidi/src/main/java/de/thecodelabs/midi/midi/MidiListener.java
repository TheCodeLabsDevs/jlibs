package de.thecodelabs.midi.midi;

public interface MidiListener
{
	void onMidiMessage(MidiCommand midiEvent);
}
