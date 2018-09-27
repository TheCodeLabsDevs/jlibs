package de.tobias.midi.device;

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
}
