package de.tobias.utils.settings.xml.handler;

import java.util.Set;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLDeserialzer;
import de.tobias.utils.settings.xml.XMLFormatStrings;
import de.tobias.utils.settings.xml.XMLSerializer;

public class SetHandler implements ClassHandler {

	@Override
	public Element serialze(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException {
		Set<?> list = (Set<?>) obj;

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
		return Set.class;
	}

	@Override
	public String getType() {
		return XMLFormatStrings.SET_ELEMENT;
	}

	@Override
	public <T extends SettingsSerializable> Object deserialzeList(Element element)
			throws IllegalAccessException, InstantiationException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		String attributeValue = element.attributeValue("class");
		if (attributeValue != null) {
			try {
				Class<?> setClass = Class.forName(attributeValue);
				@SuppressWarnings("unchecked") Set<Object> list = (Set<Object>) setClass.newInstance();

				for (Object itemObj : element.elements(XMLFormatStrings.ITEM_ELEMENT)) {
					if (itemObj instanceof Element) {
						Element itemElement = (Element) itemObj;

						Object data = XMLDeserialzer.deserialzeData(itemElement);
						if (data != null)
							list.add(data);
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
