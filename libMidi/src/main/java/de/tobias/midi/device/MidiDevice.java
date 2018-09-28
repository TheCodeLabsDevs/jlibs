package de.tobias.midi.device;

import de.tobias.midi.MidiListener;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class MidiDevice
{
	protected static List<MidiListener> midiListenerList;
	protected static List<MidiListener> removableList;

	static
	{
		midiListenerList = new ArrayList<>();
		removableList = new LinkedList<>();
	}

	public static void addMidiListener(MidiListener midiListener)
	{
		removableList.remove(midiListener);
		midiListenerList.add(midiListener);
	}

	public static void removeMidiListener(MidiListener midiListener)
	{
		removableList.add(midiListener);
	}

	private final MidiDeviceInfo midiDeviceInfo;

	public MidiDevice(MidiDeviceInfo midiDeviceInfo)
	{
		this.midiDeviceInfo = midiDeviceInfo;
	}

	public MidiDeviceInfo getMidiDeviceInfo()
	{
		return midiDeviceInfo;
	}

	public abstract void closeDevice() throws Exception;

	public abstract void open() throws Exception;

	public abstract boolean isOpen();

}
