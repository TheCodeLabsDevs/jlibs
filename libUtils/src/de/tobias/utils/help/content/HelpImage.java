package de.tobias.utils.help.content;

import java.io.IOException;
import java.util.Base64;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.hp.gagawa.java.elements.Img;

import de.tobias.utils.help.HelpContent;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.help.ui.HelpMapViewController;

/**
 * Repräsentiert ein Bild
 * 
 * @author tobias
 *
 */
public class HelpImage extends HelpContent {

	/**
	 * Bildquelle
	 */
	private String source;

	/**
	 * Erstellt ein neues Element
	 * 
	 * @param helpMap
	 *            Referenz zur HelpMap
	 */
	public HelpImage(HelpMap helpMap) {
		super(helpMap);
	}

	@Override
	public String toString() {
		return source;
	}

	@Override
	public Node getNode(HelpMapViewController hmvc) {
		Image image = getHelpMap().findImage(source);
		return new ImageView(image);
	}

	@Override
	public com.hp.gagawa.java.Node getHTMLNode() {
		byte[] data = null;
		try {
			data = getHelpMap().loadImage(source);
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("data:image/png;base64,");
		sb.append(Base64.getEncoder().encodeToString(data));
		
		Img image = new Img(null, sb.toString());
		return image;
	}

	@Override
	public HelpContent load(Element root) {
		this.source = root.getTextContent();
		return this;
	}

	@Override
	public Element save(Element e, Document document) {
		e.setTextContent(source);
		return e;
	}
}
