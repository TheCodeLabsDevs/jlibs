package de.tobias.midi;

import de.tobias.midi.device.JavaDeviceManager;
import de.tobias.midi.device.MacMidiDeviceManager;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.device.MidiDeviceManager;
import de.tobias.utils.util.OS;

import javax.sound.midi.*;
import javax.sound.midi.MidiDevice.Info;
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
		if(OS.isMacOS())
		{
			midiDeviceManager = new MacMidiDeviceManager();
		}
		else
		{
			midiDeviceManager = new JavaDeviceManager();
		}
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

	public static Info[] getMidiDevices_()
	{
		return MidiSystem.getMidiDeviceInfo();
	}

	public boolean isModeSupported(Mode mode)
	{
		return getDevice(mode) != null;
	}

	public void lookupMidiDevice(String name, Mode... modes) throws IllegalArgumentException, MidiUnavailableException, NullPointerException
	{
		boolean first = true;

		Info input = null;
		Info output = null;
		for(Info item : Midi.getMidiDevices_())
		{
			if(item.getName().equals(name))
			{
				if(first)
				{
					input = item;
					first = false;
				}
				else
				{
					output = item;
				}
			}
		}

		if(input == null)
		{
			throw new MidiUnavailableException("Midi device " + name + " unavailable");
		}

		for(Mode mode : modes)
		{
			if(mode == Mode.INPUT)
			{
				setMidiInputDevice(input);
			}
			else if(mode == Mode.OUTPUT)
			{
				setMidiOutputDevice(output);
			}
		}
	}

	private void setMidiInputDevice(Info input) throws MidiUnavailableException, IllegalArgumentException
	{
		MidiDevice newInputDevice = MidiSystem.getMidiDevice(input);

		if(newInputDevice == null)
		{
			return;
		}

		if(this.inputDevice == newInputDevice)
		{
			return;
		}

		// Close Old Devices
		closeInput();

		this.inputDevice = newInputDevice;

		Transmitter trans = inputDevice.getTransmitter();
		trans.setReceiver(new MidiMessageHandler());

		inputDevice.open();
	}

	private void setMidiOutputDevice(Info output) throws MidiUnavailableException, IllegalArgumentException
	{
		MidiDevice newOutputDevice = MidiSystem.getMidiDevice(output);

		if(newOutputDevice == null)
		{
			return;
		}

		if(this.outputDevice == newOutputDevice)
		{
			return;
		}

		// Close Old Devices
		closeOutput();

		this.outputDevice = newOutputDevice;
		outputDevice.open();
	}

	public void closeInput()
	{
		try
		{
			if(inputDevice != null)
			{
				inputDevice.getTransmitter().close();
				inputDevice.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void closeOutput()
	{
		try
		{
			if(outputDevice != null)
			{
				outputDevice.getReceiver().close();
				outputDevice.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void close()
	{
		closeInput();
		closeOutput();
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
					outputDevice.getReceiver().send(message, -1);
				}
				catch(InvalidMidiDataException | MidiUnavailableException e)
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
