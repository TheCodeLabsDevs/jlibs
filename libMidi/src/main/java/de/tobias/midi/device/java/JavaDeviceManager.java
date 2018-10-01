package de.tobias.midi.device.java;

import de.tobias.midi.Midi;
import de.tobias.midi.device.MidiDeviceInfo;
import de.tobias.midi.device.MidiDeviceManager;

import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import java.util.stream.Stream;

public class JavaDeviceManager implements MidiDeviceManager
{
	@Override
	public MidiDeviceInfo[] listDevices()
	{
		final MidiDevice.Info[] midiDeviceInfo = MidiSystem.getMidiDeviceInfo();
		return Stream.of(midiDeviceInfo).map(device -> new MidiDeviceInfo(device.getName(), device.getDescription())).toArray(MidiDeviceInfo[]::new);
	}

	@Override
	public JavaMidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws Exception
	{
		final JavaMidiDevice javaMidiDevice = new JavaMidiDevice(deviceInfo);
		javaMidiDevice.lookupMidiDevice(deviceInfo, modes);
		return javaMidiDevice;
	}
}
