package de.thecodelabs.storage;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.function.Consumer;

public class XMLUtils {

	public static Document load(Path path) throws SAXException, IOException, ParserConfigurationException {
		return load(path.toUri().toURL());
	}

	public static Document load(URL url) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = docBuilder.parse(url.openStream());
		doc.getDocumentElement().normalize();
		return doc;
	}

	public static void forEach(NodeList list, Consumer<Element> consumer) {
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				consumer.accept(e);
			}
		}
	}

	public static void forEach(Element root, Consumer<Element> consumer) {
		forEach(root.getChildNodes(), consumer);
	}

	public static Element findElementByTag(Element root, String tag) {
		NodeList list = root.getElementsByTagName(tag);
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element e = (Element) child;
				return e;
			}
		}
		return null;
	}

	public static void saveDocument(Path path, Document doc) throws ParserConfigurationException, IOException,
			TransformerException {
		// Speichern
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(Files.newOutputStream(path, StandardOpenOption.CREATE));
		// Style
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
	}

	public static void saveNewDocument(Path path, Consumer<Document> runnable) throws ParserConfigurationException, IOException,
			TransformerException {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();

		runnable.accept(doc);

		// Speichern
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(Files.newOutputStream(path, StandardOpenOption.CREATE));
		// Style
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
	}
}
