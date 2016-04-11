package de.tobias.utils.help.content;

import javafx.scene.Node;
import javafx.scene.control.Separator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.gagawa.java.elements.Hr;

import de.tobias.utils.help.HelpContent;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;

/**
 * Repräsentiert ein Separator
 * 
 * @author tobias
 *
 */
public class HelpSeparator extends HelpContent {

	/**
	 * Erstellt ein neues Element
	 * 
	 * @param helpMap
	 *            Referenz zur HelpMap
	 */
	public HelpSeparator(HelpMap helpMap) {
		super(helpMap);
	}

	@Override
	public Node getNode(HelpMapViewController hmvc) {
		return new Separator();
	}

	@Override
	public com.hp.gagawa.java.Node getHTMLNode() {
		return new Hr();
	}
	
	@Override
	public HelpContent load(Element root) {
		return this;
	}

	@Override
	public Element save(Element e, Document document) {
		return e;
	}

}
