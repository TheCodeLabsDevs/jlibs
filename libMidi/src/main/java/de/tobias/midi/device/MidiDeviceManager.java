package de.tobias.midi.device;

import de.tobias.midi.Midi;

public interface MidiDeviceManager
{
	MidiDeviceInfo[] listDevices();

	MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws Exception;
}
