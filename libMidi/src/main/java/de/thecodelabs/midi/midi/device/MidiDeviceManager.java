package de.thecodelabs.midi.midi.device;

import de.thecodelabs.midi.midi.Midi;

import javax.sound.midi.MidiUnavailableException;
import java.util.Collection;

public interface MidiDeviceManager
{
	Collection<MidiDeviceInfo> listDevices();

	MidiDevice openDevice(MidiDeviceInfo deviceInfo, Midi.Mode... modes) throws MidiUnavailableException;
}
