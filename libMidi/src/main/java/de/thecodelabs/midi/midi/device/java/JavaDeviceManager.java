package de.thecodelabs.midi.midi.device.java;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.device.MidiDevice;
import de.thecodelabs.midi.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.device.MidiDeviceManager;
import de.thecodelabs.midi.midi.message.MidiInputPublisher;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.List;
import java.util.stream.Stream;

import static javax.sound.midi.MidiDevice.Info;

public class JavaDeviceManager implements MidiDeviceManager
{
	@Override
	public List<MidiDeviceInfo> listDevices()
	{
		final Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
		return Stream.of(midiDeviceInfo).map(device -> new MidiDeviceInfo(device.getName(), device.getDescription(), device.getVendor())).toList();
	}

	@Override
	public MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws MidiUnavailableException
	{
		final JavaMidiDevice javaMidiDevice = new JavaMidiDevice(new MidiInputPublisher(), deviceInfo);
		javaMidiDevice.lookupMidiDevice(deviceInfo, modes);
		return javaMidiDevice;
	}
}
