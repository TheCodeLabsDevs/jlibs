package de.tobias.utils.ui.scene;

import java.util.LinkedList;
import java.util.Queue;

import org.controlsfx.control.action.Action;

import de.tobias.utils.util.Worker;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.image.ImageView;

public class NotificationPane extends org.controlsfx.control.NotificationPane {

	private ImageView errorIcon = new ImageView("org/controlsfx/dialog/dialog-error.png");
	private Queue<String> errorQueue = new LinkedList<>();

	public NotificationPane(Node node) {
		super(node);

		setOnHidden(event -> {
			if (!errorQueue.isEmpty()) {
				show(errorQueue.poll(), errorIcon);
			}
		});
		errorIcon.setFitWidth(24);
		errorIcon.setFitHeight(24);

	}

	@Override
	public void show(String text) {
		setGraphic(null);
		getActions().clear();
		super.show(text);
	}

	@Override
	public void show(String text, Node graphic, Action... actions) {
		getActions().clear();
		setGraphic(null);
		super.show(text, graphic, actions);
	}

	public void showAndHide(String text, long duration) {
		showAndHide(text, duration, null);
	}

	public void showAndHide(String text, long duration, Runnable finish) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showAndHide(text, duration, finish));
		} else {
			setGraphic(null);
			show(text);
			if (duration != -1)
				Worker.runLater(() -> {
					try {
						Thread.sleep(duration);
					} catch (InterruptedException e) {}
					Platform.runLater(() -> {
						hide();
						if (finish != null)
							finish.run();
					});
				});
		}
	}

	public void showError(String error) {
		errorQueue.add(error);

		if (!isShowing()) {
			show(errorQueue.poll(), errorIcon);
		}
	}
}
