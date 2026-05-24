package de.thecodelabs.midi.midi.device.java;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.midi.message.MidiInputPublisher;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static javax.sound.midi.MidiDevice.Info;

public class JavaDeviceManager implements MidiDeviceManager
{
	@Override
	public Collection<MidiDeviceInfo> listDevices()
	{
		final Map<String, MidiDeviceInfo> byName = new LinkedHashMap<>();
		for(final Info info : MidiSystem.getMidiDeviceInfo())
		{
			byName.putIfAbsent(info.getName(), new MidiDeviceInfo(info.getName(), info.getDescription(), info.getVendor()));
		}
		return byName.values();
	}

	@Override
	public MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws MidiUnavailableException
	{
		final JavaMidiDevice javaMidiDevice = new JavaMidiDevice(new MidiInputPublisher(), deviceInfo);
		javaMidiDevice.lookupMidiDevice(deviceInfo, modes);
		return javaMidiDevice;
	}
}
