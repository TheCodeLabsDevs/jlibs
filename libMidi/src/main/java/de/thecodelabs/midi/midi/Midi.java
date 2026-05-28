package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.mapping.Mapping;
import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.action.ActionHandler;
import de.thecodelabs.midi.mapping.action.ActionHandlerResolver;
import de.thecodelabs.midi.mapping.feedback.*;
import de.thecodelabs.midi.mapping.input.InputKey;
import de.thecodelabs.midi.midi.device.*;
import de.thecodelabs.midi.midi.device.coremidi.CoreMidiDeviceManager;
import de.thecodelabs.midi.midi.device.java.JavaDeviceManager;
import de.thecodelabs.utils.util.OS;

import javax.sound.midi.MidiUnavailableException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class Midi implements AutoCloseable
{
	public enum Mode
	{
		INPUT, OUTPUT
	}

	private final MidiDeviceManager midiDeviceManager;
	private final List<MidiListener> midiListeners = new LinkedList<>();

	private MidiDevice device;

	public Midi()
	{
		midiDeviceManager = OS.isMacOS() ? new CoreMidiDeviceManager() : new JavaDeviceManager();
	}

	public Midi(MidiDeviceManager midiDeviceManager)
	{
		this.midiDeviceManager = midiDeviceManager;
	}

	public Collection<MidiDeviceInfo> getMidiDevices()
	{
		return midiDeviceManager.listDevices();
	}

	public Optional<MidiDeviceInfo> getMidiDeviceInfo(String name)
	{
		return getMidiDevices().stream()
				.filter(info -> info.name().equals(name))
				.findAny();
	}

	public void addMidiListener(MidiListener listener)
	{
		midiListeners.add(listener);
	}

	public void removeMidiListener(MidiListener listener)
	{
		midiListeners.remove(listener);
	}

	public MidiDevice getDevice()
	{
		return device;
	}

	public MidiDevice openDevice(MidiDeviceInfo deviceInfo, Mode... modes) throws MidiUnavailableException
	{
		if(modes == null || modes.length == 0)
		{
			modes = new Mode[]{Mode.INPUT, Mode.OUTPUT};
		}
		device = midiDeviceManager.openDevice(deviceInfo, modes);
		midiListeners.forEach(listener -> listener.onDeviceOpen(device));
		return device;
	}

	public void clearFeedback()
	{
		midiListeners.forEach(listener -> listener.onFeedbackClear(device));
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void showCurrentFeedbackState(Mapping mapping, ActionHandlerResolver actionHandlerResolver, FeedbackValueWriterResolver feedbackValueWriterResolver)
	{
		for(InputKey inputKey : mapping.getAllInputKeys())
		{
			if(!(inputKey instanceof FeedbackProvider feedbackProvider))
			{
				continue;
			}
			final Action action = mapping.getAction(inputKey);
			final ActionHandler actionHandler = actionHandlerResolver.resolve(action);

			final FeedbackState currentState = actionHandler.getCurrentState(action);
			if(currentState == null)
			{
				continue;
			}

			final FeedbackValue feedbackValue = feedbackProvider.getFeedbackValueForState(currentState);
			final FeedbackValueWriter valueWriter = feedbackValueWriterResolver.resolve(inputKey, feedbackValue);
			if (valueWriter == null) {
				continue;
			}

			valueWriter.write(inputKey, feedbackValue);
		}
	}

	public void close() throws CloseException
	{
		try
		{
			if(device != null)
			{
				device.closeDevice();
			}
			midiDeviceManager.close();
		}
		catch(Exception e)
		{
			throw new CloseException(e);
		}
	}

	public boolean isOpen()
	{
		if(device == null)
		{
			return false;
		}
		return device.isOpen();
	}
}
