package de.tobias.utils.settings.xml;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;

public interface ClassHandler {

	public Element serialze(Element rootElement, Object obj) throws IllegalArgumentException, IllegalAccessException;

	public Class<?> getClassType();

	public String getType();

	public <T extends SettingsSerializable> Object deserialzeList(Element element)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException;
}
