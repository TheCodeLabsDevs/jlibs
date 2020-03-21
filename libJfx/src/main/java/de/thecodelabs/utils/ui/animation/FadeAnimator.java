package de.thecodelabs.utils.ui.animation;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

public class FadeAnimator {
	public static void fade(Node node, double from, double to, Duration duration) {
		FadeTransition transition = new FadeTransition(duration, node);
		transition.setFromValue(from);
		transition.setToValue(to);
		transition.playFromStart();
	}
}
