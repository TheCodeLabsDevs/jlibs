package de.tobias.utils.settings;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

public class UserDefaults {

	private HashMap<String, Object> data = new HashMap<>();

	private static HashMap<Class<? extends Object>, Serializer<? extends Object>> dataLoaders = new HashMap<>();

	public interface Serializer<T extends Object> {

		T get(Element element);

		void set(Object t, Element element);
	}

	public UserDefaults() {}

	public Object getData(String key) {
		return data.get(key);
	}

	public void setData(String key, Object data) {
		this.data.put(key, data);
	}

	public void clearData(String key) {
		data.remove(key);
	}

	public void clear() {
		data.clear();
	}

	public static UserDefaults load(Path path) throws DocumentException, IOException, ClassNotFoundException {
		UserDefaults defaults = new UserDefaults();

		if (Files.exists(path)) {
			SAXReader reader = new SAXReader();
			Document document = reader.read(Files.newInputStream(path));

			Element root = document.getRootElement();
			for (Object item : root.elements("item")) {
				Element itemElement = (Element) item;
				String key = itemElement.attributeValue("key");

				defaults.setData(key, loadElement(itemElement));
			}
		}

		return defaults;
	}

	public static Object loadElement(Element itemElement) {
		String type = itemElement.attributeValue("type");

		Class<?> clazz = null;
		try {
			clazz = Class.forName(type);
		} catch (Exception e) {
			for (Class<?> loaderClass : dataLoaders.keySet()) {
				if (loaderClass.getName().equals(type)) {
					clazz = loaderClass;
				}
			}
		}

		if (clazz == Integer.class || clazz == Integer.TYPE) {
			String data = itemElement.attributeValue("data");
			return Integer.valueOf(data);
		} else if (clazz == Long.class || clazz == Long.TYPE) {
			String data = itemElement.attributeValue("data");
			return Long.valueOf(data);
		} else if (clazz == Short.class || clazz == Short.TYPE) {
			String data = itemElement.attributeValue("data");
			return Short.valueOf(data);
		} else if (clazz == Byte.class || clazz == Byte.TYPE) {
			String data = itemElement.attributeValue("data");
			return Byte.valueOf(data);
		} else if (clazz == Float.class || clazz == Float.TYPE) {
			String data = itemElement.attributeValue("data");
			return Float.valueOf(data);
		} else if (clazz == Double.class || clazz == Double.TYPE) {
			String data = itemElement.attributeValue("data");
			return Double.valueOf(data);
		} else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
			String data = itemElement.attributeValue("data");
			return Boolean.valueOf(data);
		} else if (clazz == String.class) {
			return itemElement.attributeValue("data");
		} else {
			for (Class<?> loaderClass : dataLoaders.keySet()) {
				if (loaderClass.getName().equals(type)) {
					return dataLoaders.get(loaderClass).get(itemElement);
				}
			}
		}
		return null;
	}

	public void save(Path path) throws IOException {
		Document document = DocumentHelper.createDocument();
		Element root = document.addElement("UserDefaults");
		for (String key : data.keySet()) {
			Element itemElement = root.addElement("item");
			save(itemElement, data.get(key), key);

		}
		if (Files.notExists(path)) {
			Files.createDirectories(path.getParent());
			Files.createFile(path);
		}
		XMLWriter writer = new XMLWriter(Files.newOutputStream(path), OutputFormat.createPrettyPrint());
		writer.write(document);
		writer.close();
	}

	public static void save(Element itemElement, Object t, String key) {
		if (t != null) {
			Class<?> clazz = t.getClass();

			itemElement.addAttribute("key", key);
			itemElement.addAttribute("type", clazz.getName());

			if (clazz == Integer.class || clazz == Integer.TYPE) {
				itemElement.addAttribute("data", t.toString());
			} else if (clazz == Long.class || clazz == Long.TYPE) {
				itemElement.addAttribute("data", t.toString());
			} else if (clazz == Short.class || clazz == Short.TYPE) {
				itemElement.addAttribute("data", t.toString());
			} else if (clazz == Byte.class || clazz == Byte.TYPE) {
				itemElement.addAttribute("data", t.toString());
			} else if (clazz == Float.class || clazz == Float.TYPE) {
				itemElement.addAttribute("data", t.toString());
			} else if (clazz == Double.class || clazz == Double.TYPE) {
				itemElement.addAttribute("data", t.toString());
			} else if (clazz == Boolean.class || clazz == Boolean.TYPE) {
				itemElement.addAttribute("data", t.toString());
			} else if (clazz == String.class) {
				itemElement.addAttribute("data", t.toString());
			} else {
				for (Class<?> loaderClass : dataLoaders.keySet()) {
					if (loaderClass.getName().equals(clazz.getName())) {
						dataLoaders.get(loaderClass).set(t, itemElement);
					}
				}
			}
		}
	}

	public static <T> void registerLoader(Serializer<T> loader, Class<T> clazz) {
		dataLoaders.put(clazz, loader);
	}
}
