package de.thecodelabs.midi.midi.device;

public interface MidiListener
{
	void onDeviceOpen(MidiDevice device);

	void onFeedbackClear(MidiDevice device);
}
