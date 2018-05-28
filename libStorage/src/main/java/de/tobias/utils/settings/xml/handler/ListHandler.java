package de.tobias.utils.settings.xml.handler;

import java.util.List;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLDeserializer;
import de.tobias.utils.settings.xml.XMLFormatStrings;
import de.tobias.utils.settings.xml.XMLSerializer;

@Deprecated
public class ListHandler implements ClassHandler {

	@Override
	public Element serialize(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException {
		List<?> list = (List<?>) obj;

		Element element = rootElement.addElement(getType());
		element.addAttribute("class", list.getClass().getName()); // TODO Fix

		for (Object item : list) {
			Element itemElement = element.addElement(XMLFormatStrings.ITEM_ELEMENT);
			XMLSerializer.serializeData(item, itemElement);
		}
		return element;
	}

	@Override
	public Class<?> getClassType() {
		return List.class;
	}

	@Override
	public String getType() {
		return XMLFormatStrings.LIST_ELEMENT;
	}

	@Override
	public <T extends SettingsSerializable> Object deserializeClass(Element element) throws Exception {
		String attributeValue = element.attributeValue("class");
		if (attributeValue != null) {
			try {
				Class<?> listClass = Class.forName(attributeValue);
				@SuppressWarnings("unchecked") List<Object> list = (List<Object>) listClass.newInstance();

				for (Object itemObj : element.elements(XMLFormatStrings.ITEM_ELEMENT)) {
					if (itemObj instanceof Element) {
						Element itemElement = (Element) itemObj;

						Object data = XMLDeserializer.deserializeData(itemElement);
						if (data != null) {
							list.add(data);
						}
					}
				}
				return list;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
