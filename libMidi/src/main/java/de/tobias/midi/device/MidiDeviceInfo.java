package de.tobias.midi.device;

public class MidiDeviceInfo
{
	private final String name;
	private final String displayName;
	private final String manufacturer;

	public MidiDeviceInfo(String name, String displayName, String manufacturer)
	{
		this.name = name;
		this.displayName = displayName;
		this.manufacturer = manufacturer;
	}

	public String getName()
	{
		return name;
	}

	public String getDisplayName()
	{
		return displayName;
	}

	public String getManufacturer()
	{
		return manufacturer;
	}

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
