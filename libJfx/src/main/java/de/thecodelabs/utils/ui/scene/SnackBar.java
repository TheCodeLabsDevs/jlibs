package de.thecodelabs.utils.ui.scene;

import de.thecodelabs.utils.threading.Worker;
import javafx.application.Platform;
import javafx.scene.Node;
import org.controlsfx.control.NotificationPane;
import org.controlsfx.control.action.Action;

import java.util.LinkedList;
import java.util.Queue;

public class SnackBar extends NotificationPane
{
	private final Node errorIconNode;
	private final Queue<String> errorQueue = new LinkedList<>();

	public SnackBar(Node parent, Node errorIconNode) {
		super(parent);

		this.errorIconNode = errorIconNode;
		setOnHidden(event -> {
			if (!errorQueue.isEmpty()) {
				show(errorQueue.poll(), errorIconNode);
			}
		});
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
			show(errorQueue.poll(), errorIconNode);
		}
	}
}
