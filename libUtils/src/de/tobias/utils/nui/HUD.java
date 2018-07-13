package de.tobias.utils.nui;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class HUD extends AnchorPane {

	private FadeTransition fadeTransition;
	private Pos pos = Pos.BOTTOM_CENTER;

	public HUD(Node content) {
		content.setMouseTransparent(true);
		getChildren().add(content);
		NVC.setAnchor(content, 0, 0, 0, 0);
		setBackground(new Background(new BackgroundFill(Color.rgb(30, 30, 30, 0.7), new CornerRadii(20), null)));
	}

	public void setPosition(Pos pos) {
		this.pos = pos;
	}

	public void addToParent(Pane pane) {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(() -> addToParent(pane));
			return;
		}

		if (!pane.getChildren().contains(pane)) {
			if (fadeTransition != null) {
				try {
					((Pane) getParent()).getChildren().remove(this);
				} catch (Exception e) {}
				fadeTransition.stop();
			}
			fadeTransition = new FadeTransition();
			fadeTransition.setNode(this);
			fadeTransition.setFromValue(0);
			fadeTransition.setToValue(1);
			fadeTransition.setOnFinished(event ->
			{
				fadeTransition = null;
			});
			fadeTransition.play();

			pane.getChildren().add(this);
		}

		switch (pos) {
		case TOP_CENTER:
		case CENTER:
		case BOTTOM_CENTER:
		case BASELINE_CENTER:
			translateXProperty().bind(pane.widthProperty().divide(2).subtract(widthProperty().divide(2)));
			break;

		case TOP_LEFT:
		case CENTER_LEFT:
		case BOTTOM_LEFT:
		case BASELINE_LEFT:
			translateXProperty().bind(pane.widthProperty().divide(6));
			break;

		case TOP_RIGHT:
		case CENTER_RIGHT:
		case BOTTOM_RIGHT:
		case BASELINE_RIGHT:
			translateXProperty().bind(pane.widthProperty().subtract(pane.widthProperty().divide(6)).subtract(widthProperty()));
			break;
		}

		switch (pos) {
		case CENTER_LEFT:
		case CENTER:
		case CENTER_RIGHT:
		case BASELINE_CENTER:
		case BASELINE_LEFT:
		case BASELINE_RIGHT:
			translateYProperty().bind(pane.heightProperty().divide(2).subtract(heightProperty().divide(2)));
			break;

		case TOP_LEFT:
		case TOP_CENTER:
		case TOP_RIGHT:
			translateYProperty().bind(pane.heightProperty().divide(6));
			break;

		case BOTTOM_LEFT:
		case BOTTOM_CENTER:
		case BOTTOM_RIGHT:
			translateYProperty().bind(pane.heightProperty().subtract(pane.heightProperty().divide(6)).subtract(heightProperty()));
			break;
		}
	}

	public void removeFromParent() {
		if (!Platform.isFxApplicationThread()) {
			Platform.runLater(this::removeFromParent);
			return;
		}
		if (fadeTransition != null) {
			fadeTransition.stop();
		}
		fadeTransition = new FadeTransition();
		fadeTransition.setNode(this);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.setOnFinished(event ->
		{
			try {
				((Pane) getParent()).getChildren().remove(this);
			} catch (Exception ignored) {}
			fadeTransition = null;
		});
		fadeTransition.play();
	}
}
