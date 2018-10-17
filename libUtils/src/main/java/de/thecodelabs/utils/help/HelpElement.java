package de.thecodelabs.utils.help;

import com.hp.gagawa.java.Document;
import de.thecodelabs.utils.help.ui.HelpMapViewController;

import java.util.UUID;

/**
 * Element in einer HelpMap
 * 
 * @author tobias
 *
 */
public abstract class HelpElement implements Cloneable {

	/**
	 * Name des Elements
	 */
	protected String name;
	/**
	 * ID eines Elements
	 */
	private UUID uuid;

	private HelpMap helpMap;

	/**
	 * Erstellt ein neues HelpElement
	 * 
	 * @param name
	 *            Name des Elements
	 * @param uuid
	 *            ID des Elements
	 */
	public HelpElement(String name, UUID uuid, HelpMap helpmap) {
		this.name = name;
		this.uuid = uuid;
		this.helpMap = helpmap;
	}

	/**
	 * Name des Elements
	 * 
	 * @return Name
	 */
	public String getName() {
		return name;
	}

	/**
	 * ID des Elements
	 * 
	 * @return UUID
	 */
	public UUID getUUID() {
		return uuid;
	}

	public HelpMap getHelpMap() {
		return helpMap;
	}

	public abstract Document getHtmlDocument(HelpMapViewController hmvc, Document htmlDocument);

	@Override
	public String toString() {
		return name;
	}
}
