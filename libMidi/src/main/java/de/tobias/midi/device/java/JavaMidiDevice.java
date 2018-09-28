package de.tobias.midi.device.java;

import de.tobias.midi.MidiCommand;
import de.tobias.midi.MidiListener;
import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.event.KeyEventDispatcher;
import de.tobias.midi.event.KeyEventType;
import de.tobias.midi.mapping.KeyType;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.Arrays;

public class JavaMidiDevice extends MidiDevice implements Receiver
{
	private javax.sound.midi.MidiDevice internalDevice;

	public JavaMidiDevice(MidiDeviceInfo midiDeviceInfo, javax.sound.midi.MidiDevice internalDevice)
	{
		super(midiDeviceInfo);
		this.internalDevice = internalDevice;
	}

	public javax.sound.midi.MidiDevice getInternalDevice()
	{
		return internalDevice;
	}

	@Override
	public void send(javax.sound.midi.MidiMessage message, long timeStamp)
	{
		try
		{
			MidiCommand midiEvent = new MidiCommand(message);

			for(MidiListener listener : removableList)
			{
				midiListenerList.remove(listener);
			}
			removableList.clear();

			for(MidiListener midiListener : midiListenerList)
			{
				if(!midiEvent.isConsumed())
				{
					midiListener.onMidiMessage(midiEvent);
				}
			}

			if(message instanceof ShortMessage && !midiEvent.isConsumed())
			{
				int key = message.getMessage()[1];
				int velocity = message.getMessage()[2];

				KeyEventType type = velocity > 0 ? KeyEventType.DOWN : KeyEventType.UP;
				KeyEvent keyEvent = new KeyEvent(KeyType.MIDI, type, key);

				KeyEventDispatcher.dispatchEvent(keyEvent);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
