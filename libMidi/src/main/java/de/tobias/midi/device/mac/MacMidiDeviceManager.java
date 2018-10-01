package de.tobias.midi.device.mac;

import de.tobias.midi.Midi;
import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.device.MidiDeviceManager;

public class MacMidiDeviceManager implements MidiDeviceManager
{
	public MacMidiDeviceManager()
	{
		init();
	}

	private native void init();

	private native void deinit();

	@Override
	protected void finalize() throws Throwable
	{
		deinit();
		super.finalize();
	}

	@Override
	public native MidiDeviceInfo[] listDevices();

	@Override
	public native MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws Exception;
}
