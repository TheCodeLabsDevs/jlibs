package de.tobias.utils.help.content;

import java.util.UUID;

import javafx.scene.Node;

import org.controlsfx.control.HyperlinkLabel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.gagawa.java.elements.A;
import com.hp.gagawa.java.elements.Script;

import de.tobias.utils.help.HelpContent;
import de.tobias.utils.help.HelpElement;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;

/**
 * Repräsentiert eine Referenz auf eine andere Topic
 * 
 * @author tobias
 *
 */
public class HelpReference extends HelpContent {

	/**
	 * Referenz zur Topic
	 */
	private UUID target;
	/**
	 * Dargestellter Text
	 */
	private String text;

	/**
	 * Erstellt ein neues Element
	 * 
	 * @param helpMap
	 *            Referenz zur HelpMap
	 */
	public HelpReference(HelpMap helpMap) {
		super(helpMap);
	}

	@Override
	public Node getNode(HelpMapViewController hmvc) {
		HyperlinkLabel hyperlinkLabel = new HyperlinkLabel("[" + text + "]");
		hyperlinkLabel.setOnAction(e -> {
			HelpElement element = getHelpMap().findElement(target);
			hmvc.selectElement(element);
		});
		return hyperlinkLabel;
	}

	@Override
	public com.hp.gagawa.java.Node getHeader() {
		Script script = new Script("text/javascript");
		script.appendText("function selectElement(name){ app.select(name);}");
		return script;
	}

	@Override
	public com.hp.gagawa.java.Node getHTMLNode() {
		A a = new A();
		a.setName("alink");
		a.setAttribute("onclick", "selectElement(\'" + target + "\')");
		a.setHref("#");
		a.appendText(text);
		return a;
	}

	@Override
	public HelpContent load(Element root) {
		this.text = root.getTextContent();
		this.target = UUID.fromString(root.getAttribute(HelpMap.idItemAttributeName));
		return this;
	}

	@Override
	public Element save(Element element, Document document) {
		return null;
	}

}
