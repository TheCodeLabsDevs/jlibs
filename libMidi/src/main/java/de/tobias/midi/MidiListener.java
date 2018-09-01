package de.tobias.midi;

import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.event.KeyEventDispatcher;
import de.tobias.midi.event.KeyEventType;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import java.util.Arrays;

public class MidiListener implements Receiver
{
	@Override
	public void send(MidiMessage message, long timeStamp)
	{
		int channel = message.getMessage()[0];
		int key = message.getMessage()[1];
		int velocity = message.getMessage()[2];

		KeyEventType type = velocity > 0 ? KeyEventType.DOWN : KeyEventType.UP;
		KeyEvent keyEvent = new KeyEvent(type, key);

		KeyEventDispatcher.dispatchEvent(keyEvent);
	}

	@Override
	public void close()
	{

	}
}
