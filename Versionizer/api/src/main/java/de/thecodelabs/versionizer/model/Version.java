package de.thecodelabs.versionizer.model;

import de.thecodelabs.versionizer.config.Artifact;

import java.util.Objects;

public class Version implements Comparable<Version>
{
	private final Artifact artifact;

	private final int major;
	private final int minor;
	private final int fix;

	private final boolean snapshot;

	public Version(Artifact artifact, int major, int minor, int fix, boolean snapshot)
	{
		this.artifact = artifact;
		this.major = major;
		this.minor = minor;
		this.fix = fix;
		this.snapshot = snapshot;
	}

	public Artifact getArtifact()
	{
		return artifact;
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

	public boolean isNewerThen(Version version)
	{
		return this.compareTo(version) > 0;
	}

	@Override
	public String toString()
	{
		return "Version{" +
				"artifact=" + artifact +
				", major=" + major +
				", minor=" + minor +
				", fix=" + fix +
				", snapshot=" + snapshot +
				'}';
	}

	@Override
	public int compareTo(Version o)
	{
		if(major == o.major)
		{
			if(minor == o.minor)
			{
				if(fix == o.fix)
				{
					if(snapshot == o.snapshot)
					{
						return 0;
					}
					else if(snapshot && !o.snapshot)
					{
						return -1; // o is newer (no snapshot)
					}
					else
					{
						return 1;
					}
				}
				else
				{
					return Integer.compare(fix, o.fix);
				}
			}
			else
			{
				return Integer.compare(minor, o.minor);
			}
		}
		else
		{
			return Integer.compare(major, o.major);
		}
	}

	@Override
	public boolean equals(Object o)
	{
		if(this == o) return true;
		if(o == null || getClass() != o.getClass()) return false;
		Version version = (Version) o;
		return major == version.major &&
				minor == version.minor &&
				fix == version.fix &&
				snapshot == version.snapshot &&
				Objects.equals(artifact, version.artifact);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(artifact, major, minor, fix, snapshot);
	}
}
