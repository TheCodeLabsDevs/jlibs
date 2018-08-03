package de.tobias.utils.nui;

import javafx.scene.image.Image;

import java.util.Optional;

/**
 * Definiert einige Abstracte Schnittstellen für das Darstellen von Meldungen zu einer View.
 * 
 * @author tobias
 *
 */
public interface Alertable {

	void showErrorMessage(String message);

	void showInfoMessage(String message);

	void showErrorMessage(String message, Image icon);

	void showErrorMessage(String message, Optional<Image> icon);

	void showInfoMessage(String message, Image icon);

	void showInfoMessage(String message, String header, Image icon);

}