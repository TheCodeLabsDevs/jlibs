package de.tobias.midi;

import de.tobias.midi.event.KeyEvent;
import de.tobias.midi.event.KeyEventDispatcher;
import de.tobias.midi.event.KeyEventType;

import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MidiMessageHandler implements Receiver
{
	private static List<MidiListener> midiListenerList;
	private static List<MidiListener> removableList;

	static
	{
		midiListenerList = new ArrayList<>();
		removableList = new LinkedList<>();
	}


	@Override
	public void send(MidiMessage message, long timeStamp)
	{
		try
		{
			MidiEvent midiEvent = new MidiEvent(message);

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
				KeyEvent keyEvent = new KeyEvent(type, key);

				KeyEventDispatcher.dispatchEvent(keyEvent);
			}
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

	public static void addMidiListener(MidiListener midiListener)
	{
		removableList.remove(midiListener);
		midiListenerList.add(midiListener);
	}

	public static void removeMidiListener(MidiListener midiListener)
	{
		removableList.add(midiListener);
	}
}
