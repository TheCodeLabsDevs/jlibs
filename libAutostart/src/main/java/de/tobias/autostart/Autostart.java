package de.tobias.autostart;

import java.nio.file.Path;

/**
 * Autostartfunktionen.
 *
 * @author tobias
 * @version 1.0
 */
public interface Autostart
{

	/**
	 * Fügt ein Eintrag zur Autostartgruppe / Anmeldeobjekte hinzu
	 *
	 * @param name
	 * @param src
	 * @throws Exception
	 */
	void add(String name, Path src) throws Exception;


	/**
	 * Prüft die Liste auf einen Eintrag
	 *
	 * @param name Names des Eintrages
	 * @param src  Ort der Datei, ales Kontrollwert
	 * @return <code>true</code> Eintrag ist vorhanden und richtig
	 * @throws Exception Mögliche IOException oder ähnliches
	 */
	boolean isAutostart(String name, Path src) throws Exception;

	/**
	 * Löscht einen Eintrag
	 *
	 * @param name Names des Eintrages
	 * @throws Exception Mögliche IOException oder ähnliches
	 */
	void removeAutostart(String name) throws Exception;

	/**
	 * Identifiziert die Implementation mit einem Namen
	 *
	 * @return name
	 */
	String name();
}
