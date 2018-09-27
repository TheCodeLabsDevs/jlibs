package de.tobias.midi.device;

public abstract class MidiDevice
{
	private final MidiDeviceInfo midiDeviceInfo;

	public MidiDevice(MidiDeviceInfo midiDeviceInfo)
	{
		this.midiDeviceInfo = midiDeviceInfo;
	}

	public MidiDeviceInfo getMidiDeviceInfo()
	{
		return midiDeviceInfo;
	}

	public abstract void close() throws Exception;

	public abstract void open() throws Exception;

	public abstract boolean isOpen();

}
