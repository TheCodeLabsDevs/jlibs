package de.tobias.utils.ui;

import java.util.Optional;

import javafx.scene.image.Image;

public interface Alertable {

	public void showErrorMessage(String message);

	public void showInfoMessage(String message);

	public void showErrorMessage(String message, Image icon);

	public void showErrorMessage(String message, Optional<Image> icon);

	public void showInfoMessage(String message, Image icon);

	public void showInfoMessage(String message, String header, Image icon);

}