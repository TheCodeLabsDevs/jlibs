package de.thecodelabs.storage.xml;

import org.dom4j.Element;

/**
 * Schnittstelle um ein Object auf einem XML Tree zu deserialisieren.
 *
 * @param <T> Typ der Daten
 * @author tobias
 */
public interface XMLDeserializer<T>
{

	/**
	 * Lädt ein Object auf XML Daten.
	 *
	 * @param element XML Objekt
	 * @return Daten aus dem XML
	 */
	T loadElement(Element element);

}
