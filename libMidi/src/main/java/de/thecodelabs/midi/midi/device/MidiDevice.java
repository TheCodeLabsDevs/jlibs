package de.thecodelabs.midi.midi.device;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.message.MidiInputPublisher;
import de.thecodelabs.midi.midi.message.MidiMessage;

public abstract class MidiDevice
{
	private final MidiDeviceInfo midiDeviceInfo;
	protected final MidiInputPublisher publisher;

	protected MidiDevice(MidiDeviceInfo midiDeviceInfo, MidiInputPublisher publisher)
	{
		this.midiDeviceInfo = midiDeviceInfo;
		this.publisher = publisher;
	}

	public MidiDeviceInfo getMidiDeviceInfo()
	{
		return midiDeviceInfo;
	}

	public abstract void sendMidiMessage(MidiMessage midiEvent);

	public abstract void closeDevice() throws Exception;

	public abstract boolean isOpen();

	public abstract boolean isModeSupported(Midi.Mode mode);

	public MidiInputPublisher getPublisher()
	{
		return publisher;
	}
}
