package de.thecodelabs.midi.midi.device.java;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.message.MidiInputPublisher;
import de.thecodelabs.midi.midi.message.MidiMessage;

import javax.sound.midi.*;

class JavaMidiDevice extends MidiDevice implements Receiver
{
	private javax.sound.midi.MidiDevice internalInputDevice;
	private Transmitter internalTransmitter;
	private javax.sound.midi.MidiDevice internalOutputDevice;
	private Receiver internalReceiver;

	JavaMidiDevice(final MidiInputPublisher publisher, final MidiDeviceInfo midiDeviceInfo)
	{
		super(midiDeviceInfo, publisher);
	}

	@Override
	public void send(javax.sound.midi.MidiMessage message, long timeStamp)
	{
		final MidiMessage midiCommand = new MidiMessage(message);
		publisher.publish(midiCommand);
	}

	@Override
	public void sendMidiMessage(MidiMessage midiEvent)
	{
		if(isModeSupported(Midi.Mode.OUTPUT))
		{
			try
			{
				final byte[] payload = midiEvent.getPayload();
				final ShortMessage message = new ShortMessage(midiEvent.getMessageType().getMidiValue() + midiEvent.getChannel(), payload[0], payload[1]);
				internalReceiver.send(message, -1);
			}
			catch(InvalidMidiDataException e)
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
		closeInput();
		closeOutput();
	}

	private void closeInput()
	{
		if(internalTransmitter != null)
		{
			internalTransmitter.close();
			internalTransmitter = null;
		}
		if(internalInputDevice != null)
		{
			internalInputDevice.close();
			internalInputDevice = null;
		}
	}

	private void closeOutput()
	{
		if(internalReceiver != null)
		{
			internalReceiver.close();
			internalReceiver = null;
		}
		if(internalOutputDevice != null)
		{
			internalOutputDevice.close();
			internalOutputDevice = null;
		}
	}

	@Override
	public boolean isOpen()
	{
		return (internalInputDevice != null && internalInputDevice.isOpen())
		       || (internalOutputDevice != null && internalOutputDevice.isOpen());
	}

	void lookupMidiDevice(final MidiDeviceInfo deviceInfo, final Midi.Mode... modes) throws MidiUnavailableException
	{
		final JavaMidiDeviceInfo result = getMidiDeviceInfo(deviceInfo);
		if(result.inputInfo() == null && result.outputInfo() == null)
		{
			throw new MidiUnavailableException("Midi device " + result.name() + " unavailable");
		}

		for(final Midi.Mode mode : modes)
		{
			switch(mode)
			{
				case INPUT ->
				{
					if(result.inputInfo() != null)
					{
						setMidiInputDevice(result.inputInfo());
					}
				}
				case OUTPUT ->
				{
					if(result.outputInfo() != null)
					{
						setMidiOutputDevice(result.outputInfo());
					}
				}
			}
		}
	}

	private static JavaMidiDeviceInfo getMidiDeviceInfo(MidiDeviceInfo deviceInfo)
	{
		final String name = deviceInfo.name();
		javax.sound.midi.MidiDevice.Info inputInfo = null;
		javax.sound.midi.MidiDevice.Info outputInfo = null;

		for(final javax.sound.midi.MidiDevice.Info info : MidiSystem.getMidiDeviceInfo())
		{
			if(!info.getName().equals(name))
			{
				continue;
			}

			try
			{
				final javax.sound.midi.MidiDevice device = MidiSystem.getMidiDevice(info);
				if(device.getMaxTransmitters() != 0)
				{
					inputInfo = info;
				}
				if(device.getMaxReceivers() != 0)
				{
					outputInfo = info;
				}
			}
			catch(MidiUnavailableException _)
			{
				// Nothing to do
			}
		}
		return new JavaMidiDeviceInfo(name, inputInfo, outputInfo);
	}

	private record JavaMidiDeviceInfo(String name, javax.sound.midi.MidiDevice.Info inputInfo,
	                                  javax.sound.midi.MidiDevice.Info outputInfo)
	{
	}

	private void setMidiInputDevice(javax.sound.midi.MidiDevice.Info input) throws MidiUnavailableException, IllegalArgumentException
	{
		final javax.sound.midi.MidiDevice newInputDevice = MidiSystem.getMidiDevice(input);
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
		internalInputDevice.open();
		internalTransmitter = internalInputDevice.getTransmitter();
		internalTransmitter.setReceiver(this);
	}

	private void setMidiOutputDevice(javax.sound.midi.MidiDevice.Info output) throws MidiUnavailableException, IllegalArgumentException
	{
		final javax.sound.midi.MidiDevice newOutputDevice = MidiSystem.getMidiDevice(output);
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
		internalReceiver = internalOutputDevice.getReceiver();
	}

	@Override
	public boolean isModeSupported(Midi.Mode mode)
	{
		return switch(mode)
		{
			case INPUT -> internalInputDevice != null;
			case OUTPUT -> internalOutputDevice != null;
		};
	}
}
