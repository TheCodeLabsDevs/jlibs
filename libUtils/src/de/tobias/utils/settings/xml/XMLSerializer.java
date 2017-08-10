package de.tobias.utils.settings.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.Storable;
import de.tobias.utils.settings.xml.handler.ColorHandler;
import de.tobias.utils.settings.xml.handler.DurationHandler;
import de.tobias.utils.settings.xml.handler.ListHandler;
import de.tobias.utils.settings.xml.handler.MapHandler;
import de.tobias.utils.settings.xml.handler.PathHandler;
import de.tobias.utils.settings.xml.handler.PropertyHandler;
import de.tobias.utils.settings.xml.handler.SetHandler;

public class XMLSerializer {

	static {
		handlers = new HashMap<>();
		register(new ColorHandler());
		register(new DurationHandler());
		register(new ListHandler());
		register(new SetHandler());
		register(new MapHandler());
		register(new PropertyHandler());
		register(new PathHandler());
	}

	public static HashMap<Class<?>, ClassHandler> handlers;

	public static void register(ClassHandler handler) {
		handlers.put(handler.getClassType(), handler);
	}

	// - class attr: class des wrapper types (e.g. map, list)
	// - type attr: if supertype is a wrapper then the type attr is the class of the subtype (e.g. Property->Contains Float) else type
	// indicates the type of the variable Field->ContainsDouble
	public static <T extends SettingsSerializable> void serialize(T instance, Element rootElement)
			throws IllegalArgumentException, IllegalAccessException {
		if (rootElement == null) {
			throw new IllegalArgumentException("XML Element cannot be null");
		}
		if (instance == null) {
			throw new IllegalArgumentException("Object cannot be null");
		}

		Class<? extends SettingsSerializable> instanceClazz = instance.getClass();
		rootElement.addAttribute(XMLFormatStrings.TYPE_ATTR, instanceClazz.getName());

		for (Field field : instanceClazz.getDeclaredFields()) {
			if (!Modifier.isTransient(field.getModifiers())) {
				if (field.isAnnotationPresent(Storable.class)) {
					field.setAccessible(true);

					Object data = field.get(instance);
					if (data != null) {
						Element resElement = null;

						if (field.getType().isPrimitive() || field.getType() == String.class || field.getType().isEnum()) {
							resElement = serializeField(rootElement, data);
						} else if (field.getType().isArray()) {
							resElement = serizalieArray(rootElement, data);
						} else {
							resElement = serializeClass(rootElement, data, false);
						}

						if (resElement != null) {
							resElement.addAttribute(XMLFormatStrings.NAME_ATTR, field.getName());
						}
					}
				}
			}
		}
	}

	public static Element serializeClass(Element rootElement, Object data, boolean nestedType) throws IllegalAccessException {
		for (Class<?> handlerClass : handlers.keySet()) {
			if (handlerClass.isAssignableFrom(data.getClass())) {
				ClassHandler classHandler = handlers.get(handlerClass);
				Element element = classHandler.serialze(rootElement, data);
				if (element != null) {
					if (nestedType)
						rootElement.addAttribute(XMLFormatStrings.TYPE_ATTR, classHandler.getType()); // subtype
				}
				return element;
			}
		}

		if (data instanceof SettingsSerializable) {
			Element element = rootElement.addElement(XMLFormatStrings.CLASS_ELEMENT);
			serialize((SettingsSerializable) data, element);
			return element;
		}
		System.err.println(data + " cannot be serialzed");
		return null;
	}

	private static Element serializeField(Element rootElement, Object data) throws IllegalAccessException, IllegalArgumentException {
		Element element = rootElement.addElement(XMLFormatStrings.FIELD_ELEMENT);
		serializeData(data, element);
		return element;
	}

	private static Element serizalieArray(Element rootElement, Object data) throws IllegalAccessException {
		Element element = rootElement.addElement(XMLFormatStrings.ARRAY_ELEMENT);
		element.addAttribute(XMLFormatStrings.TYPE_ATTR, data.getClass().getName());

		int lenght = Array.getLength(data);
		element.addAttribute(XMLFormatStrings.LENGTH_ATTR, String.valueOf(lenght));

		for (int i = 0; i < lenght; i++) {
			element.addElement(XMLFormatStrings.ITEM_ELEMENT).addText(Array.get(data, i).toString());
		}
		return element;
	}

	public static void serializeData(Object data, Element element) throws IllegalAccessException {
		Class<? extends Object> type = data.getClass();
		if (type == Integer.class || type == Integer.TYPE) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type == Long.class || type == Long.TYPE) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type == Short.class || type == Short.TYPE) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type == Byte.class || type == Byte.TYPE) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type == Float.class || type == Float.TYPE) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type == Double.class || type == Double.TYPE) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type == Boolean.class || type == Boolean.TYPE) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type == String.class) {
			element.addText(data.toString());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else if (type.isEnum()) {
			element.addText(((Enum<?>) data).name());
			element.addAttribute(XMLFormatStrings.TYPE_ATTR, type.getName());
		} else {
			XMLSerializer.serializeClass(element, data, true);
		}
	}
}
