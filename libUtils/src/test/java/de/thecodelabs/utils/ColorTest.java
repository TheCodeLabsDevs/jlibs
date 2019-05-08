package de.thecodelabs.utils;

import de.thecodelabs.utils.util.Color;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorTest
{
	@Test
	public void shortHexToColor()
	{
		final Color color = new Color("#FFF");
		assertEquals(255, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(255, color.getBlue());
		assertEquals(255, color.getAlpha());
	}

	@Test
	public void longHexToColor()
	{
		final Color color = new Color("#FFFFFF");
		assertEquals(255, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(255, color.getBlue());
		assertEquals(255, color.getAlpha());
	}

	@Test
	public void longWithAlphaHexToColor()
	{
		final Color color = new Color("#FFFFFF01");
		assertEquals(255, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(255, color.getBlue());
		assertEquals(1, color.getAlpha());
	}

	@Test(expected = NumberFormatException.class)
	public void invalidHexToColor()
	{
		new Color("#q23tr234");
	}

	@Test(expected = IllegalArgumentException.class)
	public void invalidArgumentHexToColor()
	{
		new Color("#4321");
	}

	@Test
	public void withoutHashTagWithAlphaHexToColor()
	{
		final Color color = new Color("FFFFFF01");
		assertEquals(255, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(255, color.getBlue());
		assertEquals(1, color.getAlpha());
	}

	@Test
	public void floatValues()
	{
		final Color color = new Color(0.5f, 0.5f, 0.5f);
		assertEquals(127, color.getRed());
		assertEquals(127, color.getGreen());
		assertEquals(127, color.getBlue());
		assertEquals(255, color.getAlpha());
	}

	@Test
	public void floatValuesAlpha()
	{
		final Color color = new Color(0.5f, 0.5f, 0.5f, 0.5f);
		assertEquals(127, color.getRed());
		assertEquals(127, color.getGreen());
		assertEquals(127, color.getBlue());
		assertEquals(127, color.getAlpha());
	}

	@Test
	public void constants()
	{
		final Color color = Color.WHITE;
		assertEquals(255, color.getRed());
		assertEquals(255, color.getGreen());
		assertEquals(255, color.getBlue());
		assertEquals(255, color.getAlpha());
	}
}
