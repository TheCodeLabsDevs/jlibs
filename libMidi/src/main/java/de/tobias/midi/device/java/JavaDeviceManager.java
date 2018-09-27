package de.tobias.midi.device.java;

import de.tobias.midi.Midi;
import de.tobias.midi.MidiMessageHandler;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.device.MidiDeviceManager;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Transmitter;
import java.util.stream.Stream;

public class JavaDeviceManager implements MidiDeviceManager
{
	private JavaMidiDevice inputDevice;
	private JavaMidiDevice outputDevice;

	@Override
	public MidiDeviceInfo[] listDevices()
	{
		final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
		return Stream.of(midiDeviceInfo).map(device -> new MidiDeviceInfo(device.getName(), device.getDescription())).toArray(MidiDeviceInfo[]::new);
	}

	@Override
	public de.tobias.midi.device.MidiDevice openInputDevice(MidiDeviceInfo deviceInfo) throws MidiUnavailableException
	{
		lookupMidiDevice(deviceInfo, Midi.Mode.INPUT);
		return inputDevice;
	}

	@Override
	public JavaMidiDevice openOutputDevice(MidiDeviceInfo deviceInfo) throws MidiUnavailableException
	{
		lookupMidiDevice(deviceInfo, Midi.Mode.OUTPUT);
		return outputDevice;
	}

	private void lookupMidiDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws IllegalArgumentException, MidiUnavailableException, NullPointerException
	{
		final String name = deviceInfo.getName();
		boolean first = true;

		MidiDevice.Info input = null;
		MidiDevice.Info output = null;

		for(MidiDevice.Info item : MidiSystem.getMidiDeviceInfo())
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

		for(Midi.Mode mode : modes)
		{
			if(mode == Midi.Mode.INPUT)
			{
				setMidiInputDevice(deviceInfo, input);
			}
			else if(mode == Midi.Mode.OUTPUT)
			{
				setMidiOutputDevice(deviceInfo, output);
			}
		}
	}

	private void setMidiInputDevice(MidiDeviceInfo midiDeviceInfo, MidiDevice.Info input) throws MidiUnavailableException, IllegalArgumentException
	{
		MidiDevice newInputDevice = MidiSystem.getMidiDevice(input);

		if(newInputDevice == null)
		{
			return;
		}

		if(this.inputDevice != null && this.inputDevice.getInternalDevice() == newInputDevice)
		{
			return;
		}

		// Close Old Devices
		closeInput();

		this.inputDevice = new JavaMidiDevice(midiDeviceInfo, newInputDevice);

		Transmitter trans = inputDevice.getInternalDevice().getTransmitter();
		trans.setReceiver(new MidiMessageHandler());

		inputDevice.open();
	}

	private void setMidiOutputDevice(MidiDeviceInfo midiDeviceInfo, MidiDevice.Info output) throws MidiUnavailableException, IllegalArgumentException
	{
		MidiDevice newOutputDevice = MidiSystem.getMidiDevice(output);

		if(newOutputDevice == null)
		{
			return;
		}

		if(this.outputDevice != null && this.outputDevice.getInternalDevice() == newOutputDevice)
		{
			return;
		}

		// Close Old Devices
		closeOutput();

		this.outputDevice = new JavaMidiDevice(midiDeviceInfo, newOutputDevice);
		outputDevice.open();
	}


	public void closeInput()
	{
		try
		{
			if(inputDevice != null)
			{
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
				outputDevice.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

}
