package de.tobias.utils.settings.xml.handler;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLFormatStrings;

public class PathHandler implements ClassHandler {

	@Override
	public Element serialze(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException {
		Element element = rootElement.addElement(getType());
		element.addText(obj.toString());

		return element;
	}

	@Override
	public Class<?> getClassType() {
		return Path.class;
	}

	@Override
	public String getType() {
		return XMLFormatStrings.PATH_ELEMENT;
	}

	@Override
	public <T extends SettingsSerializable> Object deserialzeList(Element element) {
		String content = element.getStringValue();
		if (content != null) {
			Path path = Paths.get(content);
			return path;
		}
		return null;
	}

}
