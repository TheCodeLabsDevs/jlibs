package de.tobias.autostart;

import java.io.File;
import java.nio.file.Path;

/**
 * Autostartfunktionen.
 * 
 * @author tobias
 * @version 1.0
 *
 */
public interface Autostart {

	/**
	 * Fügt ein Eintrag zur Autostartgruppe / Anmeldeobjekte hinzu
	 * 
	 * @param name
	 *            Names des Eintrages
	 * @param src
	 *            Ort der Datei
	 * @throws Exception
	 * @see #add(String, Path)
	 */
	@Deprecated
	public default void add(String name, File src) throws Exception {

	}

	/**
	 * Fügt ein Eintrag zur Autostartgruppe / Anmeldeobjekte hinzu
	 * 
	 * @param name
	 * @param src
	 * @throws Exception
	 */
	public default void add(String name, Path src) throws Exception {
		add(name, src.toFile());
	}

	/**
	 * Prüft die Liste auf einen Eintrag
	 * 
	 * @param name
	 *            Names des Eintrages
	 * @param src
	 *            Ort der Datei, ales Kontrollwert
	 * @return <code>true</code> Eintrag ist vorhanden und richtig
	 * @throws Exception
	 *             Mögliche IOException oder ähnliches
	 * @see #isAutostart(String, Path)
	 */
	@Deprecated
	public default boolean isAutostart(String name, File src) throws Exception {
		return false;
	}

	/**
	 * Prüft die Liste auf einen Eintrag
	 * 
	 * @param name
	 *            Names des Eintrages
	 * @param src
	 *            Ort der Datei, ales Kontrollwert
	 * @return <code>true</code> Eintrag ist vorhanden und richtig
	 * @throws Exception
	 *             Mögliche IOException oder ähnliches
	 */
	public default boolean isAutostart(String name, Path src) throws Exception {
		return isAutostart(name, src.toFile());
	}

	/**
	 * Löscht einen Eintrag
	 * 
	 * @param name
	 *            Names des Eintrages
	 * @throws Exception
	 *             Mögliche IOException oder ähnliches
	 */
	public void removeAutostart(String name) throws Exception;

	/**
	 * Identifiziert die Implementation mit einem Namen
	 * 
	 * @return name
	 */
	public String name();
}
