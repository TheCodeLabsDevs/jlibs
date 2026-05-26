package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.midi.device.CloseException;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.midi.device.coremidi.CoreMidiDeviceManager;
import de.thecodelabs.midi.midi.device.java.JavaDeviceManager;
import de.thecodelabs.utils.util.OS;

import javax.sound.midi.MidiUnavailableException;
import java.util.Collection;
import java.util.Optional;

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

	public Collection<MidiDeviceInfo> getMidiDevices()
	{
		return midiDeviceManager.listDevices();
	}

	public Optional<MidiDeviceInfo> getMidiDeviceInfo(String name)
	{
		return getMidiDevices().stream()
				.filter(info -> info.name().equals(name))
				.findAny();
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
			if(device != null)
			{
				device.closeDevice();
			}
			midiDeviceManager.close();
		}
		catch(Exception e)
		{
			throw new CloseException(e);
		}
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
