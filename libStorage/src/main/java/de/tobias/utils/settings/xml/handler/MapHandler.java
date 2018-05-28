package de.tobias.utils.settings.xml.handler;

import java.util.Map;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLDeserializer;
import de.tobias.utils.settings.xml.XMLFormatStrings;
import de.tobias.utils.settings.xml.XMLSerializer;

@Deprecated
public class MapHandler implements ClassHandler {

	@Override
	public Element serialize(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException {
		@SuppressWarnings("rawtypes") Map map = (Map) obj;

		Element element = rootElement.addElement(getType());
		element.addAttribute("class", map.getClass().getName()); // TODO Fix

		for (Object item : map.keySet()) {
			Element itemElement = element.addElement(XMLFormatStrings.ITEM_ELEMENT);

			Element keyElement = itemElement.addElement(XMLFormatStrings.KEY_ELEMENT);
			XMLSerializer.serializeData(item, keyElement);
			Element valueElement = itemElement.addElement(XMLFormatStrings.VALUE_ELEMENT);
			XMLSerializer.serializeData(map.get(item), valueElement);
		}
		return element;
	}

	@Override
	public Class<?> getClassType() {
		return Map.class;
	}

	@Override
	public String getType() {
		return XMLFormatStrings.MAP_ELEMENT;
	}

	@Override
	public <T extends SettingsSerializable> Object deserializeClass(Element element) throws Exception {
		String attributeValue = element.attributeValue("class");
		if (attributeValue != null) {
			try {
				Class<?> mapClass = Class.forName(attributeValue); // TODO Fix
				@SuppressWarnings("unchecked") Map<Object, Object> map = (Map<Object, Object>) mapClass.newInstance();

				for (Object obj : element.elements(XMLFormatStrings.ITEM_ELEMENT)) {
					if (obj instanceof Element) {
						Element itemElement = (Element) obj;

						Element keyElement = itemElement.element(XMLFormatStrings.KEY_ELEMENT);
						Object key = XMLDeserializer.deserializeData(keyElement);

						Element valueElement = itemElement.element(XMLFormatStrings.VALUE_ELEMENT);
						Object value = XMLDeserializer.deserializeData(valueElement);

						if (key != null && value != null)
							map.put(key, value);
					}
				}
				return map;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
