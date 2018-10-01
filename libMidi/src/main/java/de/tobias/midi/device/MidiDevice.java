package de.tobias.midi.device;

import de.tobias.midi.MidiCommand;

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

	public abstract void sendMidiMessage(MidiCommand midiEvent);

	public abstract void closeDevice() throws Exception;

	public abstract boolean isOpen();

}
