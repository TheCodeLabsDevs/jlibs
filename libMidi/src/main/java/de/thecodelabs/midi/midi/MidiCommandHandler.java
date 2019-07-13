package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.event.KeyEvent;
import de.thecodelabs.midi.event.KeyEventDispatcher;
import de.thecodelabs.midi.event.KeyEventType;
import de.thecodelabs.midi.mapping.KeyType;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MidiCommandHandler
{
	private static MidiCommandHandler instance;

	private MidiCommandHandler()
	{
		midiListenerList = new ArrayList<>();
		removableList = new LinkedList<>();
	}

	public static MidiCommandHandler getInstance()
	{
		if(instance == null)
		{
			instance = new MidiCommandHandler();
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


	public void handleCommand(MidiCommand midiCommand)
	{
		if(!removableList.isEmpty())
		{
			for(MidiListener listener : removableList)
			{
				midiListenerList.remove(listener);
			}
			removableList.clear();
		}

		for(MidiListener midiListener : midiListenerList)
		{
			if(!midiCommand.isConsumed())
			{
				midiListener.onMidiMessage(midiCommand);
			}
		}

		if(midiCommand.getMidiCommand() != MidiCommandType.SYSTEM_EXCLUSIVE && !midiCommand.isConsumed())

		{
			int key = midiCommand.getPayload()[0];
			int velocity = midiCommand.getPayload()[1];

			KeyEventType type = velocity > 0 ? KeyEventType.DOWN : KeyEventType.UP;
			KeyEvent keyEvent = new KeyEvent(KeyType.MIDI, type, key);

			KeyEventDispatcher.dispatchEvent(keyEvent);
		}
	}
}
