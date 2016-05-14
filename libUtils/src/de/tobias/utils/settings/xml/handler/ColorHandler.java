package de.tobias.utils.settings.xml.handler;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLFormatStrings;
import javafx.scene.paint.Color;

public class ColorHandler implements ClassHandler {

	@Override
	public Element serialze(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException {
		if (obj instanceof Color) {
			Color duration = (Color) obj;
			Element element = rootElement.addElement(getType());
			element.addText(String.valueOf(duration.toString()));

			return element;
		}
		return null;
	}

	@Override
	public Class<?> getClassType() {
		return Color.class;
	}

	@Override
	public String getType() {
		return XMLFormatStrings.COLOR_ELEMENT;
	}

	@Override
	public <T extends SettingsSerializable> Object deserialzeList(Element element) {
		String content = element.getStringValue();
		if (content != null) {
			Color color = Color.web(content);
			return color;
		}
		return null;
	}

}
