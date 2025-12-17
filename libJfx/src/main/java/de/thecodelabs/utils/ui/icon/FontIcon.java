package de.thecodelabs.utils.ui.icon;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.controlsfx.tools.Duplicatable;

import java.util.HashMap;
import java.util.Map;

public class FontIcon extends Label implements Duplicatable<FontIcon>
{

	public static final String STYLE_CLASS = "fonticon";
	public static final String STYLE_CLASS_ICON = "icon";
	static String defaultFontFile = "fonts/fontawesome-webfont.ttf";

	private final String fontFile;

	private final Text text = new Text();

	private static final Map<String, Map<Integer, Font>> fonts;

	static
	{
		fonts = new HashMap<>();
	}

	public static void setDefaultFontFile(String file)
	{
		defaultFontFile = file;
	}

	private int size = 14;
	private Color color;

	public FontIcon(FontAwesomeType type)
	{
		this(new FontIconType[]{type});
	}

	public FontIcon(FontAwesomeType type, int size, String styleClass)
	{
		this(new FontIconType[]{type});
		setSize(size);
		getStyleClass().add(styleClass);
	}

	public FontIcon(String styleClass, FontAwesomeType type)
	{
		this(new FontIconType[]{type});
		getStyleClass().add(styleClass);
	}

	public FontIcon(FontIconType... types)
	{
		this(types.length > 0 ? types[0].getFontFile() : defaultFontFile, types);
	}

	public FontIcon(FontAwesomeType type, String fontFile)
	{
		this(fontFile, new FontIconType[]{type});
	}

	public FontIcon(String fontFile, FontIconType... types)
	{
		this.fontFile = fontFile;
		this.setIcons(types);
		this.loadFont();
		this.getStyleClass().remove("label");
		this.getStyleClass().add(STYLE_CLASS);
		setGraphic(text);
	}

	public void setIcons(FontIconType... types)
	{
		final StringBuilder sb = new StringBuilder();
		for(FontIconType type : types)
		{
			sb.append(type.getChar());
		}

		text.setText(sb.toString());
		text.getStyleClass().add(STYLE_CLASS_ICON);
	}

	public void loadFont()
	{
		if(!fonts.containsKey(fontFile))
		{
			fonts.put(fontFile, new HashMap<>());
		}

		final Map<Integer, Font> localFonts = fonts.get(fontFile);
		if(!localFonts.containsKey(size))
		{
			localFonts.put(size, Font.loadFont(getClass().getClassLoader().getResourceAsStream(fontFile), size));
		}

		Font font = fonts.get(fontFile).get(size);
		text.setFont(font);
	}

	public int getSize()
	{
		return size;
	}

	public void setSize(int size)
	{
		this.size = size;
		loadFont();
	}

	public Color getColor()
	{
		return color;
	}

	public void setColor(Color color)
	{
		this.color = color;
		text.setFill(color);
	}

	@Override
	public FontIcon duplicate()
	{
		final FontIcon fontIcon = new FontIcon();
		fontIcon.setText(getText());
		fontIcon.setSize(size);
		fontIcon.setColor(color);
		return fontIcon;
	}
}
