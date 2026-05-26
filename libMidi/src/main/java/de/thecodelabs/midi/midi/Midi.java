package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.midi.device.*;
import de.thecodelabs.midi.midi.device.coremidi.CoreMidiDeviceManager;
import de.thecodelabs.midi.midi.device.java.JavaDeviceManager;
import de.thecodelabs.utils.util.OS;

import javax.sound.midi.MidiUnavailableException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Midi implements AutoCloseable
{
	public enum Mode
	{
		INPUT, OUTPUT
	}

	private final MidiDeviceManager midiDeviceManager;
	private final List<MidiListener> midiListeners = new LinkedList<>();

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

	public void addMidiListener(MidiListener listener)
	{
		midiListeners.add(listener);
	}

	public void removeMidiListener(MidiListener listener)
	{
		midiListeners.remove(listener);
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
		midiListeners.forEach(listener -> listener.onDeviceOpen(device));
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
