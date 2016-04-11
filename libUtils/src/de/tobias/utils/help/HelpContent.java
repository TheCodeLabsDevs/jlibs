package de.tobias.utils.help;

import java.lang.reflect.InvocationTargetException;

import javafx.scene.Node;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.tobias.utils.help.ui.HelpMapViewController;

/**
 * Grundlage für den Inhalt einer HelpTopic.
 * 
 * @author tobias
 *
 */
public abstract class HelpContent {

	/**
	 * Referenz zur HelpMap.
	 */
	private HelpMap helpMap;

	/**
	 * Erstellt eine neue Topic in loadElement von HelpContent.
	 * 
	 * @param helpMap Referenz zur HelpMap
	 */
	public HelpContent(HelpMap helpMap) {
		this.helpMap = helpMap;
	}

	/**
	 * Referenz der Zugehörigen HelpMap.
	 * 
	 * @return helpMap
	 */
	public HelpMap getHelpMap() {
		return helpMap;
	}

	/**
	 * Erstellt ein Objekt für eine grafische Oberfläche für JavaFX.
	 * 
	 * @param hmvc
	 *            View Controller
	 * @return javafx Node
	 */
	public abstract Node getNode(HelpMapViewController hmvc);
	
	public abstract com.hp.gagawa.java.Node getHTMLNode();
	
	public com.hp.gagawa.java.Node getHeader() {
		return null;
	}
	
	/**
	 * Erstellt ein Contentobject aus einem XML Element.
	 * 
	 * @param root
	 *            Item XML Elmenet
	 * @param helpMap
	 *            Zugehörige HelpMap
	 * @return content von XML
	 */
	public static HelpContent loadElement(Element root, HelpMap helpMap) {
		try {
			String className = root.getAttribute(HelpMap.classItemAttributeName);
			Class<?> clazz = Class.forName(className);
			HelpContent content = (HelpContent) clazz.getConstructor(HelpMap.class).newInstance(helpMap);
			return content.load(root);
		} catch (ClassNotFoundException e) {
			System.err.println(e.getLocalizedMessage());
		} catch (InstantiationException e) {
			System.err.println("Wrong constructor: " + e.getLocalizedMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Erstellt ein konkretes Contentobject aus einem XML Element
	 * 
	 * @param root
	 *            XML Element
	 * @return content
	 */
	public abstract HelpContent load(Element root);

	/**
	 * Speichert ein contentobject als XML Element
	 * 
	 * @param element
	 *            Root XML Elemenet
	 * @param document
	 *            XML Document
	 * @return Bearbeitetes XML Element
	 */
	public abstract Element save(Element element, Document document);
}
