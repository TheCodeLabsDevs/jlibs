package de.thecodelabs.versionizer.model;

import de.thecodelabs.versionizer.config.Build;

public class Version implements Comparable
{
	private final Build build;

	private final int major;
	private final int minor;
	private final int fix;

	private final boolean snapshot;

	public Version(Build build, int major, int minor, int fix, boolean snapshot)
	{
		this.build = build;
		this.major = major;
		this.minor = minor;
		this.fix = fix;
		this.snapshot = snapshot;
	}

	public Build getBuild()
	{
		return build;
	}

	public int getMajor()
	{
		return major;
	}

	public int getMinor()
	{
		return minor;
	}

	public int getFix()
	{
		return fix;
	}

	public boolean isSnapshot()
	{
		return snapshot;
	}

	public String toVersionString()
	{
		String version = String.format("%d.%d.%d", major, minor, fix);
		if(isSnapshot())
		{
			version += "-SNAPSHOT";
		}
		return version;
	}

	@Override
	public String toString()
	{
		return "Version{" +
				"major=" + major +
				", minor=" + minor +
				", fix=" + fix +
				", snapshot=" + snapshot +
				'}';
	}

	public int compareTo(Object o)
	{
		if(o instanceof Version)
		{
			Version v2 = (Version) o;

			if(major == v2.major)
			{
				if(minor == v2.minor)
				{
					if(fix == v2.fix)
					{
						if(snapshot == v2.snapshot)
						{
							return 0;
						}
						else if(snapshot & !v2.snapshot)
						{
							return -1; // v2 is newer (no snapshot)
						}
						else
						{
							return 1;
						}
					}
					else
					{
						return Integer.compare(fix, v2.fix);
					}
				}
				else
				{
					return Integer.compare(minor, v2.minor);
				}
			}
			else
			{
				return Integer.compare(major, v2.major);
			}
		}
		return 0;
	}
}
