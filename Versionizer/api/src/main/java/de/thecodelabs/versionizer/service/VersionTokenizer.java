package de.thecodelabs.versionizer.service;

import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.model.Version;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionTokenizer
{
	private VersionTokenizer()
	{
	}

	public static Version getVersion(Artifact artifact)
	{
		return getVersion(artifact, artifact.getVersion());
	}

	public static Version getVersion(Artifact artifact, String value)
	{
		final Pattern pattern = Pattern.compile("(\\d+).(\\d+).(\\d+)(-SNAPSHOT)?");
		final Matcher matcher = pattern.matcher(value);

		if(matcher.find())
		{
			final int major = Integer.parseInt(matcher.group(1));
			final int minor = Integer.parseInt(matcher.group(2));
			final int fix = Integer.parseInt(matcher.group(3));
			final boolean snapshot = matcher.group(4) != null;

			return new Version(artifact, major, minor, fix, snapshot);
		}
		return null;
	}

	public static int getRevision(String value)
	{
		final Pattern pattern = Pattern.compile("\\w*-[\\d.]*-[\\d]{8}.[\\d]{6}-(\\d*).[\\w]*");
		final Matcher matcher = pattern.matcher(value);

		if(matcher.find())
		{
			return Integer.parseInt(matcher.group(1));
		}
		return 0;
	}
}
