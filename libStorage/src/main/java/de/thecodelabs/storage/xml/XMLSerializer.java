package de.thecodelabs.storage.xml;

import org.dom4j.Element;

/**
 * Schnittstelle um ein Object in eine XML Struktur zu überführen.
 *
 * @param <T> Typ der Daten
 * @author tobias
 */
public interface XMLSerializer<T>
{

	/**
	 * Überführt ein Object in eine XML Struktur. Dafür wird bereits ein XML Object angelegt, zu dem Attribute und Sub Elemente hinzugefügt
	 * werden können.
	 *
	 * @param newElement XML Object
	 * @param data       Daten
	 */
	void saveElement(Element newElement, T data);

}
