package de.tobias.midi.device.java;

import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;

import javax.sound.midi.MidiUnavailableException;

public class JavaMidiDevice extends MidiDevice
{
	private javax.sound.midi.MidiDevice internalDevice;

	public JavaMidiDevice(MidiDeviceInfo midiDeviceInfo, javax.sound.midi.MidiDevice internalDevice)
	{
		super(midiDeviceInfo);
		this.internalDevice = internalDevice;
	}

	public javax.sound.midi.MidiDevice getInternalDevice()
	{
		return internalDevice;
	}

	@Override
	public void close() throws MidiUnavailableException
	{
		internalDevice.getTransmitter().close();
		internalDevice.getReceiver().close();
		internalDevice.close();
	}

	@Override
	public void open() throws MidiUnavailableException
	{
		internalDevice.open();
	}

	@Override
	public boolean isOpen()
	{
		return internalDevice.isOpen();
	}
}
