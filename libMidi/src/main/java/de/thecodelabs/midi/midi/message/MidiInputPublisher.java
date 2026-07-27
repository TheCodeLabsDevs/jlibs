package de.thecodelabs.midi.midi.message;

import java.util.concurrent.CopyOnWriteArrayList;

public class MidiInputPublisher
{
	private record PrioritizedListener(int priority, MidiMessageListener listener) {}

	private final CopyOnWriteArrayList<PrioritizedListener> midiListenerList = new CopyOnWriteArrayList<>();

	public void addMidiListener(final MidiMessageListener midiListener)
	{
		addMidiListener(midiListener, 0);
	}

	public synchronized void addMidiListener(final MidiMessageListener midiListener, final int priority)
	{
		int index = midiListenerList.size();
		for(int i = 0; i < midiListenerList.size(); i++)
		{
			if(midiListenerList.get(i).priority() < priority)
			{
				index = i;
				break;
			}
		}
		midiListenerList.add(index, new PrioritizedListener(priority, midiListener));
	}

	public synchronized void removeMidiListener(final MidiMessageListener midiListener)
	{
		midiListenerList.removeIf(entry -> entry.listener() == midiListener);
	}

	public void publish(final MidiMessage message)
	{
		for(final PrioritizedListener entry : midiListenerList)
		{
			if(!message.isConsumed())
			{
				entry.listener().onMidiMessage(message);
			}
		}
	}
}
