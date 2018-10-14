package de.thecodelabs.versionizer.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionTokenizer
{

	public static Version getVersion(String value)
	{
		final Pattern pattern = Pattern.compile("(\\d).(\\d).(\\d)(-SNAPSHOT)?");
		final Matcher matcher = pattern.matcher(value);

		if(matcher.find())
		{
			final int major = Integer.valueOf(matcher.group(1));
			final int minor = Integer.valueOf(matcher.group(2));
			final int fix = Integer.valueOf(matcher.group(3));
			final boolean snapshot = matcher.group(4) != null;

			return new Version(major, minor, fix, snapshot);
		}
		return null;
	}
}
