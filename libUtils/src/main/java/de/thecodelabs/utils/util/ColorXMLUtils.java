package de.thecodelabs.utils.util;

import javafx.scene.paint.Color;
import org.dom4j.Element;

public class ColorXMLUtils
{
	private ColorXMLUtils()
	{
	}

	public static Element save(Element element, Color color)
	{
		return element.addAttribute("red", String.valueOf(color.getRed())).addAttribute("green", String.valueOf(color.getGreen()))
				.addAttribute("blue", String.valueOf(color.getBlue())).addAttribute("opacity", String.valueOf(color.getOpacity()));
	}

	public static Color load(Element element)
	{
		if(element != null)
		{
			double red = Double.parseDouble(element.attributeValue("red"));
			double green = Double.parseDouble(element.attributeValue("green"));
			double blue = Double.parseDouble(element.attributeValue("blue"));
			double opacity = Double.parseDouble(element.attributeValue("opacity"));
			return new Color(red, green, blue, opacity);
		}
		else
		{
			return Color.TRANSPARENT;
		}
	}
}