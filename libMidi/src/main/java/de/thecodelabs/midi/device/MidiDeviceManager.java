package de.thecodelabs.midi.device;

import de.thecodelabs.midi.midi.Midi;

import javax.sound.midi.MidiUnavailableException;

public interface MidiDeviceManager
{
	MidiDeviceInfo[] listDevices();

	MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws MidiUnavailableException;
}
