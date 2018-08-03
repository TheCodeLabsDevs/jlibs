package de.tobias.utils.nui.scene;

import de.tobias.utils.nui.NVC;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class BusyView {

	private HBox progressPane;
	private Pane root;
	private ProgressIndicator indicator;

	private Transition inTransition;
	private Transition outTransition;

	public BusyView(NVC rootViewController) {
		if (rootViewController.getParent() instanceof Pane) {
			root = (Pane) rootViewController.getParent();
			indicator = new ProgressIndicator(-1);
			indicator.setStyle("-fx-progress-color: white;");
			indicator.setPrefSize(75, 75);

			progressPane = new HBox(indicator);
			progressPane.setBackground(new Background(new BackgroundFill(new Color(0.2, 0.2, 0.2, 0.8), new CornerRadii(0.5), new Insets(0))));
			progressPane.setAlignment(Pos.CENTER);

			inTransition = createTransition(true);
			outTransition = createTransition(false);
		} else {
			throw new IllegalArgumentException("Root node is not Pane: " + rootViewController.getParent().getClass().getName());
		}
	}

	public BusyView(Pane pane) {
		root = pane;
		indicator = new ProgressIndicator(-1);
		indicator.setStyle("-fx-progress-color: white;");
		indicator.setPrefSize(75, 75);

		progressPane = new HBox(indicator);
		progressPane.setBackground(new Background(new BackgroundFill(new Color(0.2, 0.2, 0.2, 0.8), new CornerRadii(0.5), new Insets(0))));
		progressPane.setAlignment(Pos.CENTER);

		inTransition = createTransition(true);
		outTransition = createTransition(false);

	}

	public Transition getInTransition() {
		return inTransition;
	}

	public void setInTransition(Transition inTransition) {
		this.inTransition = inTransition;
	}

	public Transition getOutTransition() {
		return outTransition;
	}

	public void setOutTransition(Transition outTransition) {
		this.outTransition = outTransition;
	}

	public ProgressIndicator getIndicator() {
		return indicator;
	}

	private Transition createTransition(boolean in) {
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setNode(progressPane);

		ScaleTransition scaleTransition = new ScaleTransition();
		scaleTransition.setNode(progressPane);

		if (in) {
			fadeTransition.setFromValue(0);
			fadeTransition.setToValue(1);

			scaleTransition.setFromX(1.3);
			scaleTransition.setFromY(1.3);
			scaleTransition.setToX(1);
			scaleTransition.setToY(1);
		} else {
			fadeTransition.setFromValue(1);
			fadeTransition.setToValue(0);

			scaleTransition.setFromX(1);
			scaleTransition.setFromY(1);
			scaleTransition.setToX(1.3);
			scaleTransition.setToY(1.3);
		}

		ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);

		parallelTransition.setOnFinished((e) ->
		{
			if (!in)
				root.getChildren().remove(progressPane);
		});
		return parallelTransition;
	}

	public void showProgress(boolean b) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> showProgress(b));
			return;
		}
		
		if (b) {
			if (!root.getChildren().contains(progressPane)) {
				root.getChildren().add(progressPane);
				inTransition.play();
			}
		} else {
			outTransition.play();
		}
	}
}
