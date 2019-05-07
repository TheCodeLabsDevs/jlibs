package de.thecodelabs.utils.util;

public class Color
{
	private static final int MAX = 255;
	public static final int HEX_BASE = 16;

	private int red;
	private int green;
	private int blue;
	private int alpha;

	public Color(int red, int green, int blue)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = MAX;
	}

	public Color(int red, int green, int blue, int alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color(String hex)
	{
		hex = hex.replace("#", "");
		if(hex.length() == 3)
		{
			parseHexShort(hex);
		}
		else if(hex.length() == 6)
		{
			parseHexLong(hex);
		}
		else if(hex.length() == 8)
		{
			parseHexLongAlpha(hex);
		}
		else
		{
			throw new IllegalArgumentException(hex + " is not a valid hex code");
		}
	}

	private void parseHexLong(String hex)
	{
		long value = Long.parseLong(hex, HEX_BASE);
		this.red = (int) ((value & 0xFF0000) >> 16);
		this.green = (int) ((value & 0xFF00) >> 8);
		this.blue = (int) (value & 0xFF);
		this.alpha = MAX;
	}

	private void parseHexLongAlpha(String hex)
	{
		long value = Long.parseLong(hex, HEX_BASE);
		this.red = (int) ((value & 0xFF000000) >> 24);
		this.green = (int) ((value & 0xFF0000) >> 16);
		this.blue = (int) ((value & 0xFF00) >> 8);
		this.alpha = (int) (value & 0xFF);
	}

	private void parseHexShort(String hex)
	{
		int value = Integer.parseInt(hex, HEX_BASE);
		this.red = ((value & 0xF00) >> 8) * 0x11;
		this.green = ((value & 0xF0) >> 4) * 0x11;
		this.blue = (value & 0xF) * 0x11;
		this.alpha = MAX;
	}

	public int getRed()
	{
		return red;
	}

	public int getGreen()
	{
		return green;
	}

	public int getBlue()
	{
		return blue;
	}

	public int getAlpha()
	{
		return alpha;
	}

	public void setRed(int red)
	{
		this.red = red;
	}

	public void setGreen(int green)
	{
		this.green = green;
	}

	public void setBlue(int blue)
	{
		this.blue = blue;
	}

	public void setAlpha(int alpha)
	{
		this.alpha = alpha;
	}

	@Override
	public String toString()
	{
		return "Color{" +
				"red=" + red +
				", green=" + green +
				", blue=" + blue +
				", alpha=" + alpha +
				'}';
	}

	public String toRGBHexWithoutOpacity() {
		return ColorUtils.toRGBHexWithoutOpacity(this);
	}

	public String toRGBHex() {
		return ColorUtils.toRGBHex(this);
	}
}
