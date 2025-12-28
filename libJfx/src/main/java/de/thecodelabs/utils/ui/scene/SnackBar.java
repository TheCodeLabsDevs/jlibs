package de.thecodelabs.utils.ui.scene;

import de.thecodelabs.utils.threading.Worker;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.util.Duration;
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
		setOnHidden(_ -> {
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

	@Deprecated(forRemoval = true)
	public void showAndHide(String text, long duration) {
		showAndHide(text, duration);
	}

	public void showAndHide(String text, Duration duration) {
		showAndHide(text, duration, null);
	}

	@Deprecated(forRemoval = true)
	public void showAndHide(String text, long duration, Runnable finish)
	{
		showAndHide(text, Duration.millis(duration), finish);
	}

	public void showAndHide(String text, Duration duration, Runnable finish) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showAndHide(text, duration, finish));
		} else {
			setGraphic(null);
			show(text);
			if (duration != null)
				Worker.runLater(() -> {
					try {
						Thread.sleep((long) duration.toMillis());
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						return;
					}
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
