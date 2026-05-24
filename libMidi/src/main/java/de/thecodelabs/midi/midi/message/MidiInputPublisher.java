package de.thecodelabs.midi.midi.message;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MidiInputPublisher
{
	private final List<MidiMessageListener> midiListenerList;

	public MidiInputPublisher()
	{
		midiListenerList = new CopyOnWriteArrayList<>();
	}

	public void addMidiListener(final MidiMessageListener midiListener)
	{
		midiListenerList.add(midiListener);
	}

	public void removeMidiListener(final MidiMessageListener midiListener)
	{
		midiListenerList.remove(midiListener);
	}

	public void publish(final MidiMessage message)
	{
		for(final MidiMessageListener midiListener : midiListenerList)
		{
			if(!message.isConsumed())
			{
				midiListener.onMidiMessage(message);
			}
		}
	}
}
