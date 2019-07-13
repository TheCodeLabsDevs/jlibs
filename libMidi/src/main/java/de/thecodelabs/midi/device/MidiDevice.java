package de.thecodelabs.midi.device;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.MidiCommand;

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

	public abstract boolean isModeSupported(Midi.Mode mode);

}
