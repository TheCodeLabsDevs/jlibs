package de.thecodelabs.utils.ui.icon;

import javafx.scene.paint.Color;

public class FontIconBuilder
{
	public static FontIconBuilder builder()
	{
		return new FontIconBuilder();
	}

	private FontIconType type;
	private Integer size;
	private Color color;
	private String font;

	private FontIconBuilder()
	{
		this.font = FontIcon.defaultFontFile;
	}


	public FontIconBuilder icon(FontIconType icon)
	{
		this.type = icon;
		return this;
	}

	public FontIconBuilder size(int size)
	{
		this.size = size;
		return this;
	}

	public FontIconBuilder color(Color color)
	{
		this.color = color;
		return this;
	}

	public FontIconBuilder font(String font)
	{
		this.font = font;
		return this;
	}

	public FontIcon create()
	{
		FontIcon icon = new FontIcon(this.font, this.type);
		if(color != null)
			icon.setColor(this.color);
		if(size != null)
			icon.setSize(this.size);
		return icon;
	}
}
