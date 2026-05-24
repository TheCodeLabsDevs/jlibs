package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.midi.device.CloseException;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.midi.device.coremidi.CoreMidiDeviceManager;
import de.thecodelabs.midi.midi.device.java.JavaDeviceManager;
import de.thecodelabs.midi.midi.message.MidiMessage;
import de.thecodelabs.utils.util.OS;

import javax.sound.midi.MidiUnavailableException;

public class Midi implements AutoCloseable
{
	public enum Mode
	{
		INPUT, OUTPUT
	}

	private final MidiDeviceManager midiDeviceManager;

	private MidiDevice device;

	public Midi()
	{
		midiDeviceManager = OS.isMacOS() ? new CoreMidiDeviceManager() : new JavaDeviceManager();
	}

	public Midi(MidiDeviceManager midiDeviceManager)
	{
		this.midiDeviceManager = midiDeviceManager;
	}

	public MidiDeviceInfo[] getMidiDevices()
	{
		return midiDeviceManager.listDevices();
	}

	public MidiDeviceInfo getMidiDeviceInfo(String name)
	{
		for(MidiDeviceInfo info : getMidiDevices())
		{
			if(info.name().equals(name))
			{
				return info;
			}
		}
		return null;
	}

	public MidiDevice getDevice()
	{
		return device;
	}

	public MidiDevice openDevice(MidiDeviceInfo deviceInfo, Mode... modes) throws MidiUnavailableException
	{
		if(modes == null || modes.length == 0)
		{
			modes = new Mode[]{Mode.INPUT, Mode.OUTPUT};
		}
		device = midiDeviceManager.openDevice(deviceInfo, modes);
		return device;
	}

	public void close() throws CloseException
	{
		try
		{
			device.closeDevice();
		}
		catch(Exception e)
		{
			throw new CloseException(e);
		}
	}

	public void sendMessage(MidiMessage midiCommand)
	{
		device.sendMidiMessage(midiCommand);
	}

	public boolean isOpen()
	{
		if(device == null)
		{
			return false;
		}
		return device.isOpen();
	}
}
