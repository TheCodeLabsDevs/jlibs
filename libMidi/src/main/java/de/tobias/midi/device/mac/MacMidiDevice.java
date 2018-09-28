package de.tobias.midi.device.mac;

import de.tobias.midi.MidiCommand;
import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;

public class MacMidiDevice extends MidiDevice
{
	public MacMidiDevice(MidiDeviceInfo midiDeviceInfo)
	{
		super(midiDeviceInfo);
	}

	@Override
	public native void sendMidiMessage(MidiCommand midiEvent);

	@Override
	public native void closeDevice() throws Exception;

	@Override
	public native boolean isOpen();

	public static void handleMidiEvent(MidiCommand event)
	{
		System.out.println("Native: " + event);
	}
}
