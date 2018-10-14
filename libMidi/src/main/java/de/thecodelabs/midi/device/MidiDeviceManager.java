package de.thecodelabs.midi.device;

import de.thecodelabs.midi.Midi;

public interface MidiDeviceManager
{
	MidiDeviceInfo[] listDevices();

	MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws Exception;
}
