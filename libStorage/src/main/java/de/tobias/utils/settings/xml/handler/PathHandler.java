package de.tobias.utils.settings.xml.handler;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLFormatStrings;

@Deprecated
public class PathHandler implements ClassHandler {

	@Override
	public Element serialize(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException {
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
	public <T extends SettingsSerializable> Object deserializeClass(Element element) {
		String content = element.getStringValue();
		if (content != null) {
			return Paths.get(content);
		}
		return null;
	}

}
