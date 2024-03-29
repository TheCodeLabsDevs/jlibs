package de.thecodelabs.midi.device.java;

import de.thecodelabs.midi.device.MidiDevice;
import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.MidiCommand;
import de.thecodelabs.midi.midi.MidiCommandHandler;

import javax.sound.midi.*;

public class JavaMidiDevice extends MidiDevice implements Receiver
{
	private javax.sound.midi.MidiDevice internalInputDevice;
	private javax.sound.midi.MidiDevice internalOutputDevice;

	JavaMidiDevice(MidiDeviceInfo midiDeviceInfo)
	{
		super(midiDeviceInfo);
	}

	@Override
	public void send(MidiMessage message, long timeStamp)
	{
		MidiCommand midiCommand = new MidiCommand(message);
		MidiCommandHandler.getInstance().handleCommand(midiCommand);
	}

	@Override
	public void sendMidiMessage(MidiCommand midiEvent)
	{
		if(isModeSupported(Midi.Mode.OUTPUT))
		{
			try
			{
				final byte[] payload = midiEvent.getPayload();
				ShortMessage message = new ShortMessage(midiEvent.getMidiCommand().getMidiValue() + midiEvent.getChannel(), payload[0], payload[1]);
				internalOutputDevice.getReceiver().send(message, -1);
			}
			catch(InvalidMidiDataException | MidiUnavailableException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public void close()
	{
		// Close receiver
	}

	@Override
	public void closeDevice() throws MidiUnavailableException
	{
		closeOutput();
		closeInput();
	}

	private void closeInput() throws MidiUnavailableException
	{
		if(internalInputDevice != null)
		{
			internalInputDevice.getTransmitter().close();
			internalInputDevice.close();
		}
	}

	private void closeOutput() throws MidiUnavailableException
	{
		if(internalOutputDevice != null)
		{
			internalOutputDevice.getReceiver().close();
			internalOutputDevice.close();
		}
	}

	@Override
	public boolean isOpen()
	{
		return internalInputDevice.isOpen() || internalOutputDevice.isOpen();
	}

	void lookupMidiDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws IllegalArgumentException, MidiUnavailableException, NullPointerException
	{
		final String name = deviceInfo.getName();
		boolean first = true;

		javax.sound.midi.MidiDevice.Info input = null;
		javax.sound.midi.MidiDevice.Info output = null;

		for(javax.sound.midi.MidiDevice.Info item : MidiSystem.getMidiDeviceInfo())
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
				setMidiInputDevice(input);
			}
			else if(mode == Midi.Mode.OUTPUT)
			{
				setMidiOutputDevice(output);
			}
		}
	}

	private void setMidiInputDevice(javax.sound.midi.MidiDevice.Info input) throws MidiUnavailableException, IllegalArgumentException
	{
		javax.sound.midi.MidiDevice newInputDevice = MidiSystem.getMidiDevice(input);
		if(newInputDevice == null)
		{
			return;
		}

		// Check if old device equals new device
		if(this.internalInputDevice != null && this.internalInputDevice == newInputDevice)
		{
			return;
		}

		// Close Old Devices
		closeInput();

		this.internalInputDevice = newInputDevice;

		Transmitter trans = internalInputDevice.getTransmitter();
		trans.setReceiver(this);
		internalInputDevice.open();
	}

	private void setMidiOutputDevice(javax.sound.midi.MidiDevice.Info output) throws MidiUnavailableException, IllegalArgumentException
	{
		javax.sound.midi.MidiDevice newOutputDevice = MidiSystem.getMidiDevice(output);
		if(newOutputDevice == null)
		{
			return;
		}

		// Check if old device equals new device
		if(this.internalOutputDevice != null && this.internalOutputDevice == newOutputDevice)
		{
			return;
		}

		// Close Old Devices
		closeOutput();

		this.internalOutputDevice = newOutputDevice;
		internalOutputDevice.open();
	}

	@Override
	public boolean isModeSupported(Midi.Mode mode)
	{
		switch(mode)
		{
			case INPUT:
				return internalInputDevice != null;
			case OUTPUT:
				return internalOutputDevice != null;
		}
		throw new IllegalArgumentException("Unknown Midi Mode: " + mode);
	}
}
