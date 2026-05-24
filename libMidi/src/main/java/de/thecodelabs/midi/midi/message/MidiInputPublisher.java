package de.thecodelabs.midi.midi.message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MidiInputPublisher
{
	private final List<MidiListener> midiListenerList;

	public MidiInputPublisher()
	{
		midiListenerList = new CopyOnWriteArrayList<>();
	}

	public void addMidiListener(final MidiListener midiListener)
	{
		midiListenerList.add(midiListener);
	}

	public void removeMidiListener(final MidiListener midiListener)
	{
		midiListenerList.remove(midiListener);
	}

	public void publish(final MidiMessage message)
	{
		for(final MidiListener midiListener : midiListenerList)
		{
			if(!message.isConsumed())
			{
				midiListener.onMidiMessage(message);
			}
		}
	}
}
