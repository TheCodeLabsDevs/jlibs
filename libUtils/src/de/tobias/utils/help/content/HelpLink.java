package de.tobias.utils.help.content;

import java.awt.Desktop;
import java.net.URI;

import org.controlsfx.control.HyperlinkLabel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.gagawa.java.elements.A;

import de.tobias.utils.help.HelpContent;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Repräsentiert ein Link (Internet)
 * 
 * @author tobias
 *
 */
public class HelpLink extends HelpContent {

	/**
	 * Link zur Website
	 */
	private String target;
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
	public HelpLink(HelpMap helpMap) {
		super(helpMap);
	}

	@Override
	public Node getNode(HelpMapViewController hmvc) {
		ImageView imageView = new ImageView("de/tobias/utils/help/ui/assets/compass.png");

		HyperlinkLabel hyperlinkLabel = new HyperlinkLabel("[" + text + "]");
		hyperlinkLabel.setOnAction(e -> {
			try {
				Desktop.getDesktop().browse(new URI(target));
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		HBox box = new HBox(hyperlinkLabel, imageView);
		box.setAlignment(Pos.CENTER_LEFT);
		return box;
	}

	@Override
	public com.hp.gagawa.java.Node getHTMLNode() {
		A a = new A();
		a.setName("alink");
		a.setHref(target);
		a.appendText(text);
		return a;	}
	
	@Override
	public HelpContent load(Element root) {
		this.text = root.getTextContent();
		this.target = root.getAttribute(HelpMap.urlItemAttributeName);
		return this;
	}

	@Override
	public Element save(Element element, Document document) {
		return null;
	}

}
