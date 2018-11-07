package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.VersionTokenizer;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


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

	@Test
	public void getRevision()
	{
		assertEquals(1, VersionTokenizer.getRevision("CarrotCastle-0.0.1-20181104.201621-1.jar"));
	}

	@Test
	public void getRevisionWithMultipleDigits()
	{
		assertEquals(34, VersionTokenizer.getRevision("CarrotCastle-0.10.1-20181104.201621-34.jar"));
	}

	@Test
	public void getRevisionWithComplexArtifactId()
	{
		assertEquals(2, VersionTokenizer.getRevision("libLogger-slf4j-1.0.6-20181013.102200-2.jar"));
	}

	@Test
	public void getRevisionForReleaseName()
	{
		assertEquals(0, VersionTokenizer.getRevision("api-1.0.0.jar"));
	}
}
