package de.tobias.midi.device;

public class MidiDeviceInfo
{
	private final String name;
	private final String displayName;

	public MidiDeviceInfo(String name, String displayName)
	{
		this.name = name;
		this.displayName = displayName;
	}

	public String getName()
	{
		return name;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	@Override
	public String toString()
	{
		return "MidiDeviceInfo{" +
				"name='" + name + '\'' +
				", displayName='" + displayName + '\'' +
				'}';
	}
}
