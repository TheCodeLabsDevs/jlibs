package de.tobias.midi.device;

public class MacMidiDeviceManager implements MidiDeviceManager
{
	@Override
	public native MidiDeviceInfo[] listDevices();
}
