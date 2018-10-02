package de.tobias.midi;

import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.device.MidiDeviceManager;
import de.tobias.midi.device.java.JavaDeviceManager;
import de.tobias.utils.util.OS;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiDeviceProvider;
import uk.co.xfactorylibrarians.coremidi4j.CoreMidiException;

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

	public MidiDevice getDevice()
	{
		return device;
	}

	public void openDevice(MidiDeviceInfo deviceInfo, Mode... modes) throws Exception
	{
		if(modes == null || modes.length == 0)
		{
			modes = new Mode[]{Mode.INPUT, Mode.OUTPUT};
		}
		device = midiDeviceManager.openDevice(deviceInfo, modes);
	}

	public void close() throws Exception
	{
		device.closeDevice();
	}

	public void sendMessage(MidiCommand midiCommand)
	{
		device.sendMidiMessage(midiCommand);
	}

	public boolean isOpen()
	{
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
