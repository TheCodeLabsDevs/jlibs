package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.service.VersionTokenizer;

public class VersionTokenizerTest
{
	public static void main(String[] args)
	{
		System.out.println(VersionTokenizer.getVersion(null,"1.1.0-SNAPSHOT"));
		System.out.println(VersionTokenizer.getVersion(null, "1.1.2"));
	}
}
