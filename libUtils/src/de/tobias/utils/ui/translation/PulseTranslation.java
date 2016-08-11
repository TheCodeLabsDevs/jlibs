package de.tobias.utils.ui.translation;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.util.Duration;

public class PulseTranslation extends Transition {

	private final Interpolator WEB_EASE = Interpolator.SPLINE(0.25, 0.1, 0.25, 1);
	private final Timeline timeline;
	private final Node node;
	private boolean oldCache = false;
	private CacheHint oldCacheHint = CacheHint.DEFAULT;
	private final boolean useCache = true;

	private final DoubleProperty x = new SimpleDoubleProperty();

	/**
	 * Create new ShakeTransition
	 * 
	 * @param node
	 *            The node to affect
	 */
	public PulseTranslation(final Node node, EventHandler<ActionEvent> event, double weigth) {
		this.node = node;
		statusProperty().addListener((ov, t, newStatus) ->
		{
			switch (newStatus) {
			case RUNNING:
				starting();
				break;
			default:
				stopping();
				break;
			}
		});

		this.timeline = new Timeline(new KeyFrame(Duration.millis(0), new KeyValue(x, 1, WEB_EASE)),
				new KeyFrame(Duration.millis(200), new KeyValue(x, 1 + weigth, WEB_EASE)),
				new KeyFrame(Duration.millis(400), new KeyValue(x, 1 - weigth, WEB_EASE)),
				new KeyFrame(Duration.millis(600), new KeyValue(x, 1 + weigth, WEB_EASE)),
				new KeyFrame(Duration.millis(800), new KeyValue(x, 1 - weigth, WEB_EASE)),
				new KeyFrame(Duration.millis(1000), new KeyValue(x, 1, WEB_EASE)));
		x.addListener((ob, n, n1) ->
		{
			node.setScaleX(n1.doubleValue());
			node.setScaleY(n1.doubleValue());
		});

		setCycleDuration(Duration.seconds(1));
		setDelay(Duration.seconds(0.2));
		setOnFinished(event);
	}

	/**
	 * Called when the animation is starting
	 */
	protected final void starting() {
		if (useCache) {
			oldCache = node.isCache();
			oldCacheHint = node.getCacheHint();
			node.setCache(true);
			node.setCacheHint(CacheHint.SPEED);
		}
	}

	/**
	 * Called when the animation is stopping
	 */
	protected final void stopping() {
		if (useCache) {
			node.setCache(oldCache);
			node.setCacheHint(oldCacheHint);
		}
	}

	@Override
	protected void interpolate(double d) {
		timeline.playFrom(Duration.seconds(d));
		timeline.stop();
	}
}
