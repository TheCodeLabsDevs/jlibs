package de.tobias.utils.help.elements;

import com.hp.gagawa.java.Document;
import com.hp.gagawa.java.elements.*;
import de.tobias.utils.help.HelpElement;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;

import java.util.List;
import java.util.UUID;

/**
 * Verwaltet Child-Elemente von HelpElement
 * 
 * @author tobias
 *
 */
public class HelpCategory extends HelpElement {

	/**
	 * Child-Elemente
	 */
	private List<HelpElement> elements;

	public HelpCategory(String name, List<HelpElement> elements, UUID uuid, HelpMap helpMap) {
		super(name, uuid, helpMap);
		this.elements = elements;
	}

	/**
	 * Child-Elemente
	 * 
	 * @return Child-Elemente
	 */
	public List<HelpElement> getChildElements() {
		return elements;
	}
	@Override
	public Document getHtmlDocument(HelpMapViewController hmvc, Document htmlDocument) {
		
		// Überschrift
		H1 headline = new H1();
		headline.appendText(getName());
		htmlDocument.body.appendChild(new U().appendChild(headline));

		Ul ul = new Ul().setCSSClass("content");
		getChildElements().forEach(childItem -> {
			A a = new A();
			a.appendText(childItem.getName());
			Li li = new Li().appendChild(a);
			ul.appendChild(li);
		});
		htmlDocument.body.appendChild(ul);
		return htmlDocument;
	}

	@Override
	public String toString() {
		return name;
	}
}
