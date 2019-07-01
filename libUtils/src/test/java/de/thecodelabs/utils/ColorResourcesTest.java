package de.thecodelabs.utils;

import de.thecodelabs.utils.util.ColorResources;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorResourcesTest
{
	@Test
	public void resourcesLoadTest() {
		final ColorResources instance = ColorResources.getInstance();
		assertEquals("#244522", instance.getColor("color1"));
	}
}
