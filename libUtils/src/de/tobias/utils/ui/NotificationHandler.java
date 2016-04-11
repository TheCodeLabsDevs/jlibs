package de.tobias.utils.ui;

import org.controlsfx.control.action.Action;

public interface NotificationHandler {

	public void notify(String text, long duration);

	public void notify(String text, long duration, Runnable finish);

	public default void showError(String message) {}

	public default void show(String message, Action... action) {}

	public void hide();
}
