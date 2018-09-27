package de.tobias.midi.device;

public interface MidiDeviceManager
{
	MidiDeviceInfo[] listDevices();

	MidiDevice openInputDevice(MidiDeviceInfo deviceInfo) throws Exception;

	MidiDevice openOutputDevice(MidiDeviceInfo deviceInfo) throws Exception;
}
