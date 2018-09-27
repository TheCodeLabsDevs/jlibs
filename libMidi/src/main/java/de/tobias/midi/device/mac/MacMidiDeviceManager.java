package de.tobias.midi.device.mac;

import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.device.MidiDeviceManager;

public class MacMidiDeviceManager implements MidiDeviceManager
{
	@Override
	public native MidiDeviceInfo[] listDevices();

	@Override
	public native MidiDevice openInputDevice(MidiDeviceInfo deviceInfo);

	@Override
	public native MidiDevice openOutputDevice(MidiDeviceInfo deviceInfo);
}
