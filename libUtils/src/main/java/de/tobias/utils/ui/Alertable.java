package de.tobias.utils.ui;

/**
 * Definiert einige Abstracte Schnittstellen für das Darstellen von Meldungen zu einer View.
 *
 * @author tobias
 */
public interface Alertable {

	void showErrorMessage(String message);

	void showInfoMessage(String message);

	void showInfoMessage(String message, String header);

}