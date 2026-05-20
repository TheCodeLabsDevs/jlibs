package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.event.KeyEvent;
import de.thecodelabs.midi.event.KeyEventDispatcher;
import de.thecodelabs.midi.event.KeyEventType;
import de.thecodelabs.midi.mapping.KeyType;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class MidiInputPublisher
{
	private static MidiInputPublisher instance;

	private MidiInputPublisher()
	{
		midiListenerList = new CopyOnWriteArrayList<>();
	}

	public static MidiInputPublisher getInstance()
	{
		if(instance == null)
		{
			instance = new MidiInputPublisher();
		}
		return instance;
	}

	private final List<MidiListener> midiListenerList;

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

		final byte[] payload = message.getPayload();
		if(message.getMessageType() != null
				&& message.getMessageType() != MidiMessageType.SYSTEM_EXCLUSIVE
				&& !message.isConsumed()
				&& payload.length >= 2)
		{
			final int key = payload[0];
			final int velocity = payload[1];

			final KeyEventType type = velocity > 0 ? KeyEventType.DOWN : KeyEventType.UP;
			final KeyEvent keyEvent = new KeyEvent(KeyType.MIDI, type, key);

			KeyEventDispatcher.dispatchEvent(keyEvent);
		}
	}
}
