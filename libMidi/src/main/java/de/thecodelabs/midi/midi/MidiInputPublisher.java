package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.event.KeyEvent;
import de.thecodelabs.midi.event.KeyEventDispatcher;
import de.thecodelabs.midi.event.KeyEventType;
import de.thecodelabs.midi.mapping.KeyType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MidiInputPublisher
{
	private static MidiInputPublisher instance;

	private MidiInputPublisher()
	{
		midiListenerList = new ArrayList<>();
		removableList = new LinkedList<>();
	}

	public static MidiInputPublisher getInstance()
	{
		if(instance == null)
		{
			instance = new MidiInputPublisher();
		}
		return instance;
	}

	private List<MidiListener> midiListenerList;
	private List<MidiListener> removableList;

	public void addMidiListener(MidiListener midiListener)
	{
		removableList.remove(midiListener);
		midiListenerList.add(midiListener);
	}

	public void removeMidiListener(MidiListener midiListener)
	{
		removableList.add(midiListener);
	}


	public void publish(MidiMessage message)
	{
		if(!removableList.isEmpty())
		{
			for(MidiListener listener : removableList)
			{
				midiListenerList.remove(listener);
			}
			removableList.clear();
		}

		// Handle midi event in external listeners
		for(MidiListener midiListener : midiListenerList)
		{
			if(!message.isConsumed())
			{
				midiListener.onMidiMessage(message);
			}
		}

		// Handle midi event in internal action system
		if(message.getMessageType() != MidiMessageType.SYSTEM_EXCLUSIVE && !message.isConsumed())
		{
			final int key = message.getPayload()[0];
			final int velocity = message.getPayload()[1];

			final KeyEventType type = velocity > 0 ? KeyEventType.DOWN : KeyEventType.UP;
			final KeyEvent keyEvent = new KeyEvent(KeyType.MIDI, type, key);

			KeyEventDispatcher.dispatchEvent(keyEvent);
		}
	}
}
