package de.tobias.utils.help.content;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.gagawa.java.elements.Div;

import de.tobias.utils.help.HelpContent;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;
import javafx.scene.Node;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

/**
 * Repräsentiert ein Text
 * 
 * @author tobias
 *
 */
public class HelpText extends HelpContent {

	/**
	 * Textquelle
	 */
	private String text;

	/**
	 * Erstellt ein neues Element
	 * 
	 * @param helpMap
	 *            Referenz zur HelpMap
	 */
	public HelpText(HelpMap helpMap) {
		super(helpMap);
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public Node getNode(HelpMapViewController hmvc) {
		Text textNode = new Text(text);
		textNode.setFont(Font.font(14));

		TextFlow flow = new TextFlow(textNode);
		flow.setTextAlignment(TextAlignment.JUSTIFY);
		flow.maxWidthProperty().bind(hmvc.scrollPane.widthProperty().subtract(30));
		return flow;
	}
	
	@Override
	public com.hp.gagawa.java.Node getHTMLNode() {
		Div div = new Div();
		div.appendText(text);
		return div;
	}

	@Override
	public HelpContent load(Element root) {
		this.text = root.getTextContent().replace("\n", "").replace("\t", "");
		return this;
	}

	@Override
	public Element save(Element e, Document document) {
		e.setTextContent(text);
		return e;
	}
}
