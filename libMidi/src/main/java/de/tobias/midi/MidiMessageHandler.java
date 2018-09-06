package de.tobias.midi;

import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.event.KeyEventDispatcher;
import de.tobias.midi.event.KeyEventType;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class MidiMessageHandler implements Receiver
{
	private static List<MidiListener> midiListenerList;

	static
	{
		midiListenerList = new ArrayList<>();
	}

	@Override
	public void send(MidiMessage message, long timeStamp)
	{
		MidiEvent midiEvent = new MidiEvent(message);
		midiListenerList.forEach(midiListener -> {
			if(!midiEvent.isConsumed())
			{
				midiListener.onMidiMessage(midiEvent);
			}
		});

		if(message instanceof ShortMessage && !midiEvent.isConsumed())
		{
			int key = message.getMessage()[1];
			int velocity = message.getMessage()[2];

			KeyEventType type = velocity > 0 ? KeyEventType.DOWN : KeyEventType.UP;
			KeyEvent keyEvent = new KeyEvent(type, key);

			KeyEventDispatcher.dispatchEvent(keyEvent);
		}
	}

	@Override
	public void close()
	{
	}

	public static void addMidiListener(MidiListener midiListener)
	{
		midiListenerList.add(midiListener);
	}

	public static void removeMidiListener(MidiListener midiListener)
	{
		midiListenerList.remove(midiListener);
	}
}
