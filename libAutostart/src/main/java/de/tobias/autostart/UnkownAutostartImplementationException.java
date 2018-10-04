package de.tobias.autostart;

/**
 * Eine Implementierung ist nicht vorhanden
 * 
 * @author tobias
 * @version 1.0
 */
public class UnkownAutostartImplementationException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * Fehlerhafte Suche
	 * 
	 * @param criteria
	 *            Aktueller Suchwert
	 */
	public UnkownAutostartImplementationException(String criteria) {
		super(criteria + " is unkown.");
	}
}
