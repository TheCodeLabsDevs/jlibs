package de.thecodelabs.midi;

public interface MidiListener
{
	void onMidiMessage(MidiCommand midiEvent);
}
