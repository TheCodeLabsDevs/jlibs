package de.tobias.utils.nui;

import javafx.scene.image.Image;

import java.util.Optional;

/**
 * Definiert einige Abstracte Schnittstellen für das Darstellen von Meldungen zu einer View.
 * 
 * @author tobias - s0553746
 *
 */
public interface Alertable {

	public void showErrorMessage(String message);

	public void showInfoMessage(String message);

	public void showErrorMessage(String message, Image icon);

	public void showErrorMessage(String message, Optional<Image> icon);

	public void showInfoMessage(String message, Image icon);

	public void showInfoMessage(String message, String header, Image icon);

}