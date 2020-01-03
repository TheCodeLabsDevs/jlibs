package de.thecodelabs.midi.midi;

import de.thecodelabs.midi.Mapping;
import de.thecodelabs.midi.action.Action;
import de.thecodelabs.midi.action.ActionHandler;
import de.thecodelabs.midi.action.ActionRegistry;
import de.thecodelabs.midi.device.CloseException;
import de.thecodelabs.midi.device.MidiDevice;
import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.device.java.JavaDeviceManager;
import de.thecodelabs.midi.feedback.FeedbackType;
import de.thecodelabs.midi.mapping.MidiKey;
import de.thecodelabs.midi.midi.feedback.MidiFeedbackTranscript;
import de.thecodelabs.midi.midi.feedback.MidiFeedbackTranscriptionRegistry;
import de.thecodelabs.utils.util.OS;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiDeviceProvider;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiException;

import javax.sound.midi.MidiUnavailableException;

public class Midi implements AutoCloseable
{
	private static Midi INSTANCE;
	private static boolean useNative = true;

	public enum Mode
	{
		INPUT, OUTPUT
	}

	private MidiDeviceManager midiDeviceManager;

	private MidiDevice device;
	private MidiFeedbackTranscript feedbackTranscript;

	public static Midi getInstance()
	{
		if(INSTANCE == null)
		{
			INSTANCE = new Midi();
		}
		return INSTANCE;
	}

	private Midi()
	{
		if(OS.isMacOS() && useNative)
		{
			try
			{
				CoreMidiDeviceProvider.isLibraryLoaded();
			}
			catch(CoreMidiException e)
			{
				throw new RuntimeException(e);
			}
		}
		midiDeviceManager = new JavaDeviceManager();
	}

	public MidiDeviceInfo[] getMidiDevices()
	{
		return midiDeviceManager.listDevices();
	}

	public MidiDeviceInfo getMidiDeviceInfo(String name)
	{
		for(MidiDeviceInfo info : getMidiDevices())
		{
			if(info.getName().equals(name))
			{
				return info;
			}
		}
		return null;
	}

	public MidiDevice getDevice()
	{
		return device;
	}

	public MidiFeedbackTranscript getFeedbackTranscript()
	{
		return feedbackTranscript;
	}

	public void openDevice(MidiDeviceInfo deviceInfo, Mode... modes) throws MidiUnavailableException
	{
		if(modes == null || modes.length == 0)
		{
			modes = new Mode[]{Mode.INPUT, Mode.OUTPUT};
		}
		device = midiDeviceManager.openDevice(deviceInfo, modes);

		if(device.isModeSupported(Mode.OUTPUT))
		{
			feedbackTranscript = MidiFeedbackTranscriptionRegistry.getInstance().getTransripter(deviceInfo.getName());
		}
	}

	public void close() throws CloseException
	{
		try
		{
			device.closeDevice();
			feedbackTranscript = null;
		}
		catch(Exception e)
		{
			throw new CloseException(e);
		}
	}

	public void showFeedback()
	{
		for(Action action : Mapping.getCurrentMapping().getActions())
		{
			showFeedback(action);
		}
	}

	public void showFeedback(Action action)
	{
		ActionHandler handler = ActionRegistry.getActionHandler(action.getActionType());
		final FeedbackType currentFeedbackType = handler.getCurrentFeedbackType(action);

		for(MidiKey key : action.getKeysForType(MidiKey.class))
		{
			Midi.getInstance().sendFeedback(key, currentFeedbackType);
		}
	}

	public void sendFeedback(MidiKey key, FeedbackType feedbackType)
	{
		if(feedbackTranscript != null)
		{
			feedbackTranscript.sendFeedback(key, feedbackType);
		}
	}

	public void clearFeedback()
	{
		if(feedbackTranscript != null)
		{
			feedbackTranscript.clearFeedback();
		}
	}

	public void sendMessage(MidiCommand midiCommand)
	{
		device.sendMidiMessage(midiCommand);
	}

	public boolean isOpen()
	{
		if(device == null)
		{
			return false;
		}
		return device.isOpen();
	}

	public static boolean isUseNative()
	{
		return useNative;
	}

	public static void setUseNative(boolean useNative)
	{
		Midi.useNative = useNative;
	}
}
