package de.tobias.utils.util;

import javafx.scene.paint.Color;

import org.dom4j.Element;

public class ColorXMLUtils {

	public static Element save(Element element, Color color) {
		return element.addAttribute("red", String.valueOf(color.getRed())).addAttribute("green", String.valueOf(color.getGreen()))
				.addAttribute("blue", String.valueOf(color.getBlue())).addAttribute("opacity", String.valueOf(color.getOpacity()));
	}

	public static Color load(Element element) {
		if (element != null) {
			double red = Double.valueOf(element.attributeValue("red"));
			double green = Double.valueOf(element.attributeValue("green"));
			double blue = Double.valueOf(element.attributeValue("blue"));
			double opacity = Double.valueOf(element.attributeValue("opacity"));
			return new Color(red, green, blue, opacity);
		} else {
			return Color.TRANSPARENT;
		}
	}
}