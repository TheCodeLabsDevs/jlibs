package de.tobias.midi.device.java;

import de.tobias.midi.MidiCommand;
import de.tobias.midi.MidiCommandHandler;
import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;

import javax.sound.midi.*;
import java.util.Arrays;

public class JavaMidiDevice extends MidiDevice implements Receiver
{
	private javax.sound.midi.MidiDevice internalDevice;

	JavaMidiDevice(MidiDeviceInfo midiDeviceInfo, javax.sound.midi.MidiDevice internalDevice)
	{
		super(midiDeviceInfo);
		this.internalDevice = internalDevice;
	}

	javax.sound.midi.MidiDevice getInternalDevice()
	{
		return internalDevice;
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
		try
		{
			ShortMessage message = new ShortMessage(midiEvent.getMidiCommand().getMidiValue(), midiEvent.getPayload()[0], midiEvent.getPayload()[1]);
			System.out.println("Send: " + Arrays.toString(message.getMessage()));
			internalDevice.getReceiver().send(message, -1);
		}
		catch(InvalidMidiDataException | MidiUnavailableException e)
		{
			throw new RuntimeException(e);
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
		internalDevice.getTransmitter().close();
		internalDevice.getReceiver().close();
		internalDevice.close();
	}

	void open() throws MidiUnavailableException
	{
		internalDevice.open();
	}

	void setReceiver(Receiver receiver) throws MidiUnavailableException
	{
		internalDevice.getTransmitter().setReceiver(receiver);
	}

	@Override
	public boolean isOpen()
	{
		return internalDevice.isOpen();
	}
}
