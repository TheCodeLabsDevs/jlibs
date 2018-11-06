package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.VersionTokenizer;
import org.junit.Test;

import static org.junit.Assert.*;


public class VersionTokenizerTest
{
	@Test
	public void testGetVersion()
	{
		Version expected = new Version(null, 1, 1, 2, false);
		assertEquals(expected, VersionTokenizer.getVersion(null, "1.1.2"));
	}
	@Test
	public void testGetVersionMultipleDigits()
	{
		Version expected = new Version(null, 1, 100, 2, false);
		assertEquals(expected, VersionTokenizer.getVersion(null, "1.100.2"));
	}

	@Test
	public void testGetVersionWithSnapshot()
	{
		Version expected = new Version(null, 1, 1, 0, true);
		assertEquals(expected, VersionTokenizer.getVersion(null, "1.1.0-SNAPSHOT"));
	}

	@Test
	public void testGetVersionInvalid()
	{
		assertNull(VersionTokenizer.getVersion(null, "1.0"));
	}

	@Test
	public void testGetVersionInvalid2()
	{
		assertNull(VersionTokenizer.getVersion(null, "xyz"));
	}
}
