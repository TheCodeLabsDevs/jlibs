package de.thecodelabs.midi.device;

public record MidiDeviceInfo(String name, String displayName, String manufacturer)
{
	@Override
	public String toString()
	{
		return "MidiDeviceInfo{" +
		       "name='" + name + '\'' +
		       ", displayName='" + displayName + '\'' +
		       ", manufacturer='" + manufacturer + '\'' +
		       '}';
	}
}
