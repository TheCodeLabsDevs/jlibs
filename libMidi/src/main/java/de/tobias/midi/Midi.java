package de.tobias.midi;

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
	}

	public static Info[] getMidiDevices()
	{
		return MidiSystem.getMidiDeviceInfo();
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

	public boolean isModeSupported(Mode mode)
	{
		return getDevice(mode) != null;
	}

	public void lookupMidiDevice(String name, Mode... modes) throws IllegalArgumentException, MidiUnavailableException, NullPointerException
	{
		boolean first = true;

		Info input = null;
		Info output = null;
		for(Info item : Midi.getMidiDevices())
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

		for(Mode mode : modes)
		{
			if(mode == Mode.INPUT)
			{
				setMidiInputDevice(input);
			}
			else if(mode == Mode.OUTPUT)
			{

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
		trans.setReceiver(new MidiInputReceiver());

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

	public void sendMessage(int midiCommand, int midiKey, int midiVelocity) throws MidiUnavailableException, InvalidMidiDataException
	{
		if(outputDevice != null)
		{
			if(midiCommand != 0)
			{
				ShortMessage message = new ShortMessage(midiCommand, midiKey, midiVelocity);
				// System.out.println("Send: " + Arrays.toString(message.getMessage()));
				outputDevice.getReceiver().send(message, -1);
			}
		}
	}

	private class MidiInputReceiver implements Receiver
	{

		@Override
		public void send(MidiMessage msg, long timeStamp)
		{
			try
			{
				System.out.println(Arrays.toString(msg.getMessage()));
//				listener.onMidiAction(msg);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		@Override
		public void close()
		{
		}

	}

	public boolean isOpen()
	{
		return inputDevice != null && outputDevice != null && inputDevice.isOpen() && outputDevice.isOpen();
	}
}
