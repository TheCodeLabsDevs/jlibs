package de.tobias.utils.help;

import de.thecodelabs.storage.XMLUtils;
import de.tobias.utils.application.ApplicationUtils;
import de.tobias.utils.application.container.PathType;
import de.tobias.utils.help.elements.HelpCategory;
import de.tobias.utils.help.elements.HelpTopic;
import de.tobias.utils.io.IOUtils;
import javafx.scene.image.Image;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Container für Hilfethemen
 * 
 * @author tobias
 *
 */
public class HelpMap {

	public static final String rootItemTagName = "helpmap";
	public static final String categoryItemTagName = "category";
	public static final String topicItemTagName = "topic";
	public static final String tagItemTagName = "tag";
	public static final String headlineItemTagName = "headline";
	public static final String contentItemTagName = "content";
	public static final String itemItemTagName = "item";
	public static final String columnItemTagName = "column";
	public static final String objectItemTagName = "object";
	public static final String entryItemTagName = "entry";

	public static final String nameItemAttributeName = "name";
	public static final String idItemAttributeName = "id";
	public static final String classItemAttributeName = "class";
	public static final String urlItemAttributeName = "url";
	public static final String versionItemAttributeName = "version";

	private static final String imagePath = "images";

	/**
	 * Root HelpElements der HelpMap
	 */
	private List<HelpElement> elements;

	/**
	 * HelpMap UUID
	 */
	private UUID helpMapUuid;

	private long version;

	/**
	 * ClassPath zur HelpMap
	 */
	private String classPath;

	/**
	 * Erstellt eine neue HelpMap (Private)
	 * 
	 * @param path
	 */
	private HelpMap(String path) {
		elements = new ArrayList<>();
		this.classPath = path;
	}

	/*
	 * Access-Methods
	 */
	/**
	 * Root Elemente
	 * 
	 * @return root Elemenete
	 */
	public List<HelpElement> getElements() {
		return elements;
	}

	public long getVersion() {
		return version;
	}

	/**
	 * Bild aus der HelpMap mit Namen laden
	 * 
	 * @param name
	 *            Bildname
	 * @return gefundenes Bild
	 */
	public Image findImage(String name) {
		return new Image(classPath + "/images/" + name);
	}

	public byte[] loadImage(String name) throws IOException {
		URL url = getClass().getClassLoader().getResource(classPath + "/" + imagePath + "/" + name);
		return IOUtils.urlToByteArray(url);
	}

	public Path getLocalResourcePath(UUID elementUuid) {
		return ApplicationUtils.getApplication().getPath(PathType.HELPMAP, helpMapUuid.toString(), elementUuid.toString() + ".html");
	}

	/**
	 * Sucht alle HelpElemente nach einem Element mit UUID ab
	 * 
	 * @param uuid
	 *            ID des gesuchtene Elements
	 * @return Element mit gesucheter UUID
	 */
	public HelpElement findElement(UUID uuid) {
		return findElements(uuid, elements);
	}

	/**
	 * Sucht Rekrusiv nach einem Element in einer Liste
	 * 
	 * @param uuid
	 *            Gesuchte UUID
	 * @param search
	 *            Liste mit vorhandenen Elementen
	 * @return Gefundenes Element
	 */
	private HelpElement findElements(UUID uuid, List<HelpElement> search) {
		for (HelpElement element : search) {
			if (element.getUUID().equals(uuid)) {
				return element;
			} else if (element instanceof HelpCategory) {
				// HelpTopic hat Child-Elements
				HelpElement e = findElements(uuid, ((HelpCategory) element).getChildElements());
				if (e != null) // Nur wenn gefunden (!= null) -> Return
					return e;
			}
		}
		return null;
	}

	/**
	 * Übergeordnetes Element
	 * 
	 * @param element
	 *            Child-Element
	 * @return Parent-Element
	 */
	@Deprecated
	public HelpElement getParent(HelpElement element) {
		if (elements.contains(element)) {
			return null;
		} else {
			for (HelpElement e : elements) {
				HelpElement result = getParent(element, e);
				if (result != null)
					return result;
			}
		}
		return null;
	}

	/**
	 * Sucht das Parent-Element von einem Child-Element
	 * 
	 * @param search
	 *            Child-Element
	 * @param current
	 *            Parent-Element
	 * @return Parent-Element (HelpCategory)
	 */
	@Deprecated
	private HelpElement getParent(HelpElement search, HelpElement current) {
		if (current instanceof HelpCategory) {
			if (((HelpCategory) current).getChildElements().contains(search)) {
				return current;
			} else {
				for (HelpElement e : ((HelpCategory) current).getChildElements()) {
					HelpElement result = getParent(search, e);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}

	/**
	 * Filtert alle Elemente nach einem Schlüsselwort
	 * 
	 * @param key
	 *            Schlüsselwort
	 * @return Ergebnis
	 */
	public List<HelpElement> search(String key) {
		return forEachHelpMap(elements, element -> {
			// Name
				if (element.getName().toLowerCase().startsWith(key.toLowerCase())) {
					return true;
				}

				// Tags
				if (element instanceof HelpTopic) {
					List<String> tags = ((HelpTopic) element).getTags();
					for (String string : tags) {
						if (string.toLowerCase().startsWith(key.toLowerCase())) {
							return true;
						}
					}
				}
				return false;
			});
	}

	/**
	 * Fügt alle HelpElemente zu einer Liste, die bei Predicate true ergeben.
	 * Die Elemente werden Rekrusiv durchsucht.
	 * 
	 * @param elements
	 *            Aktuelle Elemente
	 * @param acceptor
	 *            Prüfer
	 * @return Ergebnis
	 */
	public List<HelpElement> forEachHelpMap(List<HelpElement> elements, Predicate<HelpElement> acceptor) {
		List<HelpElement> accept = new ArrayList<>();
		for (HelpElement element : elements) {
			if (acceptor.test(element)) {
				accept.add(element);
			}
			if (element instanceof HelpCategory) {
				accept.addAll(forEachHelpMap(((HelpCategory) element).getChildElements(), acceptor));
			}
		}
		return accept;
	}

	/*
	 * Load
	 */
	/**
	 * Lädt einen HelpMap Ordner
	 * 
	 * @param path
	 *            Root Path im ClassPath zur HelpMap
	 * @return HelpMap, geladen aus einer XML Datei
	 * @throws ParserConfigurationException
	 *             XML Fehler
	 * @throws SAXException
	 *             XML Fehler
	 * @throws IOException
	 *             Datei Fehler
	 */
	public static HelpMap loadHelpMap(String path) throws Exception {
		URL contentURL = HelpMap.class.getClassLoader().getResource(path + "/index.xml");
		HelpMap helpMap = new HelpMap(path);

		Document doc = XMLUtils.load(contentURL);
		Element root = doc.getDocumentElement();

		helpMap.helpMapUuid = UUID.fromString(root.getAttribute(idItemAttributeName));
//		helpMap.version = Long.valueOf(root.getAttribute(versionItemAttributeName));
		helpMap.elements.addAll(load(root, helpMap));

		return helpMap;
	}

	/**
	 * Lädt die XML Datei Rekrusiv mit hieren HelpCategories und HelpTopics
	 * 
	 * @param root
	 *            Root XML Element
	 * @param helpMap
	 *            Referenz zur HelpMap
	 * @return Geladenen Sub-Elemente
	 */
	private static List<HelpElement> load(Element root, HelpMap helpMap) {
		List<HelpElement> elements = new ArrayList<>();
		// Root of Category
		XMLUtils.forEach(root, item -> {
			String name = item.getAttribute(nameItemAttributeName);
			UUID uuid = UUID.fromString(item.getAttribute(idItemAttributeName));

			if (item.getTagName().equals(categoryItemTagName)) {
				// Unterkategorie
				HelpCategory category = new HelpCategory(name, load(item, helpMap), uuid, helpMap);
				elements.add(category);
			} else if (item.getTagName().equals(topicItemTagName)) {
				// Topic
				List<HelpContent> contents = new ArrayList<>();
				List<String> tags = new ArrayList<>();

				HelpTopic topic = new HelpTopic(name, tags, contents, uuid, helpMap);
				elements.add(topic);

				// Inhalt (Tags, Headline, Main Content)
				XMLUtils.forEach(item.getElementsByTagName(tagItemTagName), tagItem -> {
					String tag = tagItem.getTextContent();
					tags.add(tag);
				});
				XMLUtils.forEach(item.getElementsByTagName(headlineItemTagName), headlineItem -> {
					topic.setHeadline(headlineItem.getTextContent());
				});
				XMLUtils.forEach(item.getElementsByTagName(contentItemTagName), contentItem -> {
					XMLUtils.forEach(contentItem, itemItem -> {
						if (itemItem.getTagName().equals(itemItemTagName)) {
							HelpContent content = HelpContent.loadElement(itemItem, helpMap);
							contents.add(content);
						}
					});
				});
			}
		});
		return elements;
	}

	/*
	 * Save
	 */
	/**
	 * Speichern einer HelpMap in einem XML Format
	 * 
	 * @param path
	 *            Dateipfad
	 * @throws ParserConfigurationException
	 *             XML Fehler
	 * @throws IOException
	 *             Datei Fehler
	 * @throws TransformerException
	 *             Speicher Fehler
	 */
	public void save(Path path) throws ParserConfigurationException, IOException, TransformerException {
		XMLUtils.saveNewDocument(path, doc -> {
			Element root = doc.createElement(rootItemTagName);
			saveCategory(doc, root, getElements());
			doc.appendChild(root);
		});
	}

	/**
	 * Speichert die Elemenete Rekrusiv
	 * 
	 * @param document
	 *            XML Document
	 * @param root
	 *            XML Root Element
	 * @param elements
	 *            HelpMap Child-Elements
	 */
	private static void saveCategory(Document document, Element root, List<HelpElement> elements) {
		for (HelpElement element : elements) {
			Element helpRootElement = null;

			if (element instanceof HelpCategory) {
				helpRootElement = document.createElement(HelpMap.categoryItemTagName);
				// Child Elemente
				saveCategory(document, helpRootElement, ((HelpCategory) element).getChildElements());

			} else if (element instanceof HelpTopic) {
				helpRootElement = document.createElement(topicItemTagName);

				// Headline
				Element headlineElement = document.createElement(headlineItemTagName);
				headlineElement.setTextContent(((HelpTopic) element).getHeadline());
				helpRootElement.appendChild(headlineElement);

				// Tags
				for (String tag : ((HelpTopic) element).getTags()) {
					Element tagElement = document.createElement(tagItemTagName);
					tagElement.setTextContent(tag);
					helpRootElement.appendChild(tagElement);
				}

				// Content
				Element contentContainerElement = document.createElement(contentItemTagName);
				helpRootElement.appendChild(contentContainerElement);

				for (HelpContent content : ((HelpTopic) element).getContents()) {
					Element contentElement = document.createElement(itemItemTagName);
					contentElement.setAttribute(classItemAttributeName, content.getClass().getCanonicalName());
					content.save(contentElement, document);
					contentContainerElement.appendChild(contentElement);
				}
			}
			helpRootElement.setAttribute(nameItemAttributeName, element.getName());
			helpRootElement.setAttribute(idItemAttributeName, element.getUUID().toString());

			root.appendChild(helpRootElement);
		}
	}
}
