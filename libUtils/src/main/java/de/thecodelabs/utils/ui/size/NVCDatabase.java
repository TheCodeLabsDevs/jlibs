package de.thecodelabs.utils.ui.size;

import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.application.App;
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

public class NVCDatabase {

	private static final String VIEWS_ELEMENT = "Views";
	private static final String HEIGHT_ATTR = "height";
	private static final String WIDTH_ATTR = "width";
	private static final String Y_ATTR = "y";
	private static final String X_ATTR = "x";
	private static final String NAME_ATTR = "name";
	private static final String VIEW_ELEMENT = "View";
	private static final String VIEW_STATE_PATH = "ViewState.xml";

	private static HashMap<String, NVCItem> items;

	static {
		items = new HashMap<>();
	}

	public static NVCItem getItem(Class<?> clazz) {
		String name = clazz.getName();
		if (!items.containsKey(name)) {
			items.put(name, new NVCItem());
		}
		return items.get(name);
	}

	private static boolean isLoaded = false;

	public static void load() {
		if (isLoaded) {
			return;
		}

		isLoaded = true;

		App app = ApplicationUtils.getApplication();
		if (app != null) {
			Path path = app.getPath(PathType.CONFIGURATION, VIEW_STATE_PATH);
			if (Files.exists(path)) {

				try {
					SAXReader reader = new SAXReader();
					Document document = reader.read(Files.newInputStream(path));
					Element rootElement = document.getRootElement();

					for (Object obj : rootElement.elements(VIEW_ELEMENT)) {
						if (obj instanceof Element) {
							Element child = (Element) obj;

							String name = child.attributeValue(NAME_ATTR);
							NVCItem item = new NVCItem();
							items.put(name, item);

							item.setPosX(Double.valueOf(child.attributeValue(X_ATTR)));
							item.setPosY(Double.valueOf(child.attributeValue(Y_ATTR)));
							item.setWidth(Double.valueOf(child.attributeValue(WIDTH_ATTR)));
							item.setHeight(Double.valueOf(child.attributeValue(HEIGHT_ATTR)));
						}
					}
				} catch (IOException | DocumentException ex) {
					System.err.println(ex);
				}
			}
		}
	}

	public static void save() {
		App app = ApplicationUtils.getApplication();
		if (app != null) {
			Path path = app.getPath(PathType.CONFIGURATION, VIEW_STATE_PATH);

			if (Files.notExists(path)) {
				try {
					Files.createDirectories(path.getParent());
					Files.createFile(path);
					return;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			Document document = DocumentHelper.createDocument();
			Element rootElement = document.addElement(VIEWS_ELEMENT);

			for (String key : items.keySet()) {
				NVCItem item = items.get(key);

				Element child = rootElement.addElement(VIEW_ELEMENT);
				child.addAttribute(NAME_ATTR, key);
				child.addAttribute(X_ATTR, String.valueOf(item.getPosX()));
				child.addAttribute(Y_ATTR, String.valueOf(item.getPosY()));
				child.addAttribute(WIDTH_ATTR, String.valueOf(item.getWidth()));
				child.addAttribute(HEIGHT_ATTR, String.valueOf(item.getHeight()));
			}

			try {
				XMLWriter writer = new XMLWriter(Files.newOutputStream(path), OutputFormat.createPrettyPrint());
				writer.write(document);
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
