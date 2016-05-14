package de.tobias.utils.settings.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.handler.ColorHandler;
import de.tobias.utils.settings.xml.handler.DurationHandler;
import de.tobias.utils.settings.xml.handler.ListHandler;
import de.tobias.utils.settings.xml.handler.MapHandler;
import de.tobias.utils.settings.xml.handler.PathHandler;
import de.tobias.utils.settings.xml.handler.PropertyHandler;
import de.tobias.utils.settings.xml.handler.SetHandler;

public class XMLDeserialzer {

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

	private static HashMap<String, ClassHandler> handlers;

	public static void register(ClassHandler handler) {
		handlers.put(handler.getType(), handler);
	}

	public static <T extends SettingsSerializable> T deserialze(Class<T> clazz, Element rootElement)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException, NoSuchFieldException, SecurityException {
		T instance = clazz.newInstance();

		for (Object obj : rootElement.elements()) {
			if (obj instanceof Element) {
				Element element = (Element) obj;
				String name = element.attributeValue(XMLFormatStrings.NAME_ATTR);

				Field field = clazz.getDeclaredField(name);
				if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
					field.setAccessible(true);

					Object res = null;

					if (element.getName().equals(XMLFormatStrings.FIELD_ELEMENT)) {
						res = deserialzeField(element);
					} else if (element.getName().equals(XMLFormatStrings.ARRAY_ELEMENT)) {
						res = deserialzeArray(element, name);
					} else {
						res = deserialzeClass(element);
					}

					if (res != null) {
						field.set(instance, res);
					}
				}
			}
		}

		return instance;
	}

	private static Object deserialzeField(Element element)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		Object value = deserialzeData(element);
		return value;
	}

	private static Object deserialzeArray(Element element, String name)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		String attributeValue = element.attributeValue(XMLFormatStrings.TYPE_ATTR);
		if (attributeValue != null) {
			try {
				Class<? extends Object> type = Class.forName(attributeValue);
				int length = Integer.valueOf(element.attributeValue(XMLFormatStrings.LENGTH_ATTR));

				Object array = Array.newInstance(type.getComponentType(), length);
				Class<?> componentType = array.getClass().getComponentType();

				int index = 0;
				for (Object obj : element.elements(XMLFormatStrings.ITEM_ELEMENT)) {
					if (obj instanceof Element) {
						Object value = deserialzeData((Element) obj, componentType);
						if (value != null) {
							Array.set(array, index, value);
						}
						index++;
					}
				}
				return array;
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends SettingsSerializable> Object deserialzeClass(Element element)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		if (handlers.containsKey(element.getName())) {
			Object res = handlers.get(element.getName()).deserialzeList(element);
			return res;
		} else if (element.getName().equals(XMLFormatStrings.CLASS_ELEMENT)) {
			String attributeValue = element.attributeValue(XMLFormatStrings.TYPE_ATTR);
			if (attributeValue != null) {
				try {
					Class<? extends SettingsSerializable> subClass = (Class<? extends SettingsSerializable>) Class.forName(attributeValue);
					SettingsSerializable res = deserialze(subClass, element);
					return res;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public static Object deserialzeData(Element element, @SuppressWarnings("rawtypes") Class type)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		String data = element.getStringValue();

		if (type != null) {
			if (type == Integer.class || type == Integer.TYPE) {
				return Integer.valueOf(data);
			} else if (type == Long.class || type == Long.TYPE) {
				return Long.valueOf(data);
			} else if (type == Short.class || type == Short.TYPE) {
				return Short.valueOf(data);
			} else if (type == Byte.class || type == Byte.TYPE) {
				return Byte.valueOf(data);
			} else if (type == Float.class || type == Float.TYPE) {
				return Float.valueOf(data);
			} else if (type == Double.class || type == Double.TYPE) {
				return Double.valueOf(data);
			} else if (type == Boolean.class || type == Boolean.TYPE) {
				return Boolean.valueOf(data);
			} else if (type == String.class) {
				return data;
			} else if (type.isEnum()) {
				@SuppressWarnings("unchecked") Object enumData = Enum.valueOf(type, data);
				return enumData;
			}
		} else {
			String subtype = element.attributeValue(XMLFormatStrings.TYPE_ATTR); // SubType
			if (subtype != null) {
				Element subElement = element.element(subtype);
				Object res = XMLDeserialzer.deserialzeClass(subElement);
				return res;
			}
		}
		return null;
	}

	public static Object deserialzeData(Element element)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
		if (element.attributeValue(XMLFormatStrings.TYPE_ATTR) != null) {
			try {
				Class<?> clazz = Class.forName(element.attributeValue(XMLFormatStrings.TYPE_ATTR));
				return deserialzeData(element, clazz);
			} catch (ClassNotFoundException e) {}
		}
		return deserialzeData(element, null); // SubType
	}
}
