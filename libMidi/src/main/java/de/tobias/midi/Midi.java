package de.tobias.midi;

import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.device.MidiDeviceManager;
import de.tobias.midi.device.java.JavaDeviceManager;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.Arrays;

public class Midi implements AutoCloseable
{
	private static Midi INSTANCE;

	public enum Mode
	{
		INPUT, OUTPUT
	}

	private MidiDeviceManager midiDeviceManager;

	private MidiDevice inputDevice;
	private MidiDevice outputDevice;

	public static Midi getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new Midi();
		}
		return INSTANCE;
	}

	private Midi()
	{
//		if(OS.isMacOS())
//		{
//			midiDeviceManager = new MacMidiDeviceManager();
//		}
//		else
//		{
			midiDeviceManager = new JavaDeviceManager();
//		}
	}

	public MidiDeviceInfo[] getMidiDevices()
	{
		return midiDeviceManager.listDevices();
	}

	public MidiDevice getDevice(Mode mode)
	{
		switch(mode)
		{
			case INPUT:
				return inputDevice;
			case OUTPUT:
				return outputDevice;
		}
		return null;
	}

	public void openDevice(MidiDeviceInfo deviceInfo, Mode... modes) throws Exception
	{
		for(Mode mode : modes)
		{
			if(mode == Mode.INPUT)
			{
				openInputDevice(deviceInfo);
			}
			else if(mode == Mode.OUTPUT)
			{
				openOutputDevice(deviceInfo);
			}
			else
			{
				throw new RuntimeException("Midi Mode not supported: " + mode);
			}
		}
	}

	public void openInputDevice(MidiDeviceInfo deviceInfo) throws Exception
	{
		inputDevice = midiDeviceManager.openInputDevice(deviceInfo);
	}

	public void openOutputDevice(MidiDeviceInfo deviceInfo) throws Exception
	{
		outputDevice = midiDeviceManager.openOutputDevice(deviceInfo);
	}

	public boolean isModeSupported(Mode mode)
	{
		return getDevice(mode) != null;
	}

	public void close() throws Exception
	{
		inputDevice.close();
		outputDevice.close();
	}

	public void sendMessage(int midiCommand, int midiKey, int midiVelocity)
	{
		if(isModeSupported(Mode.OUTPUT))
		{
			if(midiCommand != 0)
			{
				try
				{
					ShortMessage message = new ShortMessage(midiCommand, midiKey, midiVelocity);
					System.out.println("Send: " + Arrays.toString(message.getMessage()));
					//outputDevice.getReceiver().send(message, -1); TODO
				}
				catch(InvalidMidiDataException /*| MidiUnavailableException*/ e)
				{
					throw new RuntimeException(e);
				}
			}
		}
	}

	public boolean isOpen()
	{
		return inputDevice != null && outputDevice != null && inputDevice.isOpen() && outputDevice.isOpen();
	}
}
