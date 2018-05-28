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

@Deprecated
public class XMLDeserializer {

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

	public static <T extends SettingsSerializable> T deserialize(Class<T> clazz, Element rootElement) throws Exception {
		T instance = clazz.newInstance();

		for (Object obj : rootElement.elements()) {
			if (obj instanceof Element) {
				Element element = (Element) obj;
				String name = element.attributeValue(XMLFormatStrings.NAME_ATTR);

				Field field = clazz.getDeclaredField(name);
				if (!Modifier.isFinal(field.getModifiers()) && !Modifier.isTransient(field.getModifiers())) {
					field.setAccessible(true);

					Object res;

					switch (element.getName()) {
						case XMLFormatStrings.FIELD_ELEMENT:
							res = deserializeField(element);
							break;
						case XMLFormatStrings.ARRAY_ELEMENT:
							res = deserializeArray(element);
							break;
						default:
							res = deserializeClass(element);
							break;
					}

					if (res != null) {
						field.set(instance, res);
					}
				}
			}
		}

		return instance;
	}

	private static Object deserializeField(Element element) throws Exception {
		return deserializeData(element);
	}

	private static Object deserializeArray(Element element) throws Exception {
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
						Object value = deserializeData((Element) obj, componentType);
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
	private static <T extends SettingsSerializable> Object deserializeClass(Element element)
			throws Exception {
		if (handlers.containsKey(element.getName())) {
			return handlers.get(element.getName()).deserializeClass(element);
		} else if (element.getName().equals(XMLFormatStrings.CLASS_ELEMENT)) {
			String attributeValue = element.attributeValue(XMLFormatStrings.TYPE_ATTR);
			if (attributeValue != null) {
				try {
					Class<? extends SettingsSerializable> subClass = (Class<? extends SettingsSerializable>) Class.forName(attributeValue);
					return deserialize(subClass, element);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	private static Object deserializeData(Element element, @SuppressWarnings("rawtypes") Class type)
			throws Exception {
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
				return XMLDeserializer.deserializeClass(subElement);
			}
		}
		return null;
	}

	public static Object deserializeData(Element element) throws Exception {
		if (element.attributeValue(XMLFormatStrings.TYPE_ATTR) != null) {
			try {
				Class<?> clazz = Class.forName(element.attributeValue(XMLFormatStrings.TYPE_ATTR));
				return deserializeData(element, clazz);
			} catch (ClassNotFoundException ignored) {
			}
		}
		return deserializeData(element, null); // SubType
	}
}
