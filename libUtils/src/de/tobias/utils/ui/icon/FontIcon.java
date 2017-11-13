package de.tobias.utils.ui.icon;

import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class FontIcon extends Label {

	public static final String STYLE_CLASS = "fonticon";
	private static String FONT_FILE = "de/tobias/utils/ui/icon/fontawesome-webfont.ttf";
	
	private String fontFile = FONT_FILE;
	
	private static Map<String, Map<Integer, Font>> fonts;
	
	static {
		fonts = new HashMap<>();
	}

	public static void setDefaultFontFile(String file) {
		FONT_FILE = file;
	}

	private int size = 14;
	private Font font;
	private Color color;

	public FontIcon(FontAwesomeType type) {
		this(new FontIconType[] { type });
	}
	
	public FontIcon(String styleClass, FontAwesomeType type) {
		this(new FontIconType[] { type });
		getStyleClass().add(styleClass);
	}

	public FontIcon(FontIconType... types) {
		this(FONT_FILE, types);
	}

	public FontIcon(FontAwesomeType type, String fontFile) {
		this(new FontIconType[] { type });
	}

	public FontIcon(String fontFile, FontIconType... types) {
		this.fontFile = fontFile; 
		setIcons(types);
		loadFont();
		getStyleClass().remove("label");
		getStyleClass().add(STYLE_CLASS);
		
	}

	public void setIcons(FontIconType... types) {
		StringBuilder text = new StringBuilder();
		for (FontIconType type : types) {
			text.append(type.getChar());
		}
		setText(text.toString());
	}

	public void loadFont() {
		if (!fonts.containsKey(fontFile)) {
			fonts.put(fontFile, new HashMap<>());
		}
		
		Map<Integer, Font> localFonts = fonts.get(fontFile);
		if (!localFonts.containsKey(size)) {
			localFonts.put(size, Font.loadFont(getClass().getClassLoader().getResourceAsStream(fontFile), size));
		}
		
		font = fonts.get(fontFile).get(size);
		setFont(font);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
		loadFont();
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
		setTextFill(color);
	}
}
