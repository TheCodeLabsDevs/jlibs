package de.tobias.utils.help.content;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.gagawa.java.elements.Li;
import com.hp.gagawa.java.elements.Ul;

import de.tobias.utils.help.HelpContent;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;
import de.tobias.utils.util.XMLUtils;
import javafx.scene.Node;
import javafx.scene.control.ListView;

/**
 * Repräsentiert eine Liste
 * 
 * @author tobias
 *
 */
public class HelpList extends HelpContent {

	/**
	 * Items in der Liste
	 */
	private List<String> items;

	/**
	 * Erstellt ein neues Element
	 * 
	 * @param helpMap
	 *            Referenz zur HelpMap
	 */
	public HelpList(HelpMap helpMap) {
		super(helpMap);
		items = new ArrayList<>();
	}

	@Override
	public Node getNode(HelpMapViewController hmvc) {
		ListView<String> list = new ListView<>();
		list.getItems().addAll(items);

		list.setMinHeight(200);

		return list;
	}

	@Override
	public com.hp.gagawa.java.Node getHTMLNode() {
		Ul ul = new Ul();
		items.forEach(item -> ul.appendChild(new Li().appendText(item)));
		return ul;
	}

	@Override
	public HelpContent load(Element root) {
		XMLUtils.forEach(root, entryNode -> {
			items.add(entryNode.getTextContent());
		});
		return this;
	}

	@Override
	public Element save(Element element, Document document) {
		return null;
	}

}
