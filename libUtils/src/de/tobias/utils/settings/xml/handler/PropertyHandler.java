package de.tobias.utils.settings.xml.handler;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLDeserialzer;
import de.tobias.utils.settings.xml.XMLFormatStrings;
import de.tobias.utils.settings.xml.XMLSerializer;
import javafx.beans.property.Property;

public class PropertyHandler implements ClassHandler {

	@Override
	public Element serialze(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException {
		Property<?> prop = (Property<?>) obj;
		Object data = prop.getValue();
		if (data != null) {
			Element element = rootElement.addElement(getType());
			element.addAttribute("class", prop.getClass().getName()); // TODO Fix

			XMLSerializer.serializeData(data, element);
			return element;
		}
		return null;
	}

	@Override
	public Class<?> getClassType() {
		return Property.class;
	}

	@Override
	public String getType() {
		return XMLFormatStrings.PROPERTY_ELEMENT;
	}

	@Override
	public <T extends SettingsSerializable> Object deserialzeList(Element element)
			throws IllegalAccessException, InstantiationException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		String attributeValue = element.attributeValue("class");
		if (attributeValue != null) {
			try {
				Class<?> propClass = Class.forName(attributeValue); // TODO Fix
				@SuppressWarnings("unchecked") Property<Object> prop = (Property<Object>) propClass.newInstance();

				Object data = XMLDeserialzer.deserialzeData(element);
				if (data != null) {
					prop.setValue(data);
				}
				return prop;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
