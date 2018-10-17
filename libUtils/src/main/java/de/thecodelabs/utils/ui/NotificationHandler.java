package de.thecodelabs.utils.ui;

import org.controlsfx.control.action.Action;

public interface NotificationHandler {

	void notify(String text, long duration);

	void notify(String text, long duration, Runnable finish);

	default void show(String message, Action... action) {
	}

	void showError(String message);

	void hide();
}
