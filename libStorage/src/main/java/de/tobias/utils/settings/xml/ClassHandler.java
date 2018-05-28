package de.tobias.utils.settings.xml;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;

@Deprecated
public interface ClassHandler {

	Element serialize(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException;

	Class<?> getClassType();

	String getType();

	<T extends SettingsSerializable> Object deserializeClass(Element element) throws Exception;
}
