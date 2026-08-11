package de.thecodelabs.utils.ui.scene;

import de.thecodelabs.utils.ui.NVC;
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

public class BusyView
{

	private final Pane root;
	private HBox container;
	private Region indicator;

	private Transition inTransition;
	private Transition outTransition;

	public BusyView(NVC rootViewController)
	{
		if(rootViewController.getParent() instanceof Pane pane)
		{
			root = pane;
			initialize();
		}
		else
		{
			throw new IllegalArgumentException("Root node is not Pane: " + rootViewController.getParent().getClass().getName());
		}
	}

	public BusyView(Pane container)
	{
		root = container;
		initialize();
	}

	private void initialize()
	{
		indicator = new ProgressIndicator(-1);
		indicator.setStyle("-fx-progress-color: white;");
		indicator.setPrefSize(75, 75);

		container = new HBox(indicator);
		container.setBackground(new Background(new BackgroundFill(new Color(0.2, 0.2, 0.2, 0.8), new CornerRadii(0.5), new Insets(0))));
		container.setAlignment(Pos.CENTER);

		inTransition = createTransition(true);
		outTransition = createTransition(false);
	}

	public Region getIndicatorNode()
	{
		return indicator;
	}

	public void setIndicatorNode(Region indicator)
	{
		this.indicator = indicator;
		container.getChildren().setAll(indicator);
	}

	public Transition getInTransition()
	{
		return inTransition;
	}

	public void setInTransition(Transition inTransition)
	{
		this.inTransition = inTransition;
	}

	public Transition getOutTransition()
	{
		return outTransition;
	}

	public void setOutTransition(Transition outTransition)
	{
		this.outTransition = outTransition;
	}

	private Transition createTransition(boolean in)
	{
		FadeTransition fadeTransition = new FadeTransition();
		fadeTransition.setNode(container);

		ScaleTransition scaleTransition = new ScaleTransition();
		scaleTransition.setNode(container);

		if(in)
		{
			fadeTransition.setFromValue(0);
			fadeTransition.setToValue(1);

			scaleTransition.setFromX(1.3);
			scaleTransition.setFromY(1.3);
			scaleTransition.setToX(1);
			scaleTransition.setToY(1);
		}
		else
		{
			fadeTransition.setFromValue(1);
			fadeTransition.setToValue(0);

			scaleTransition.setFromX(1);
			scaleTransition.setFromY(1);
			scaleTransition.setToX(1.3);
			scaleTransition.setToY(1.3);
		}

		ParallelTransition parallelTransition = new ParallelTransition(fadeTransition, scaleTransition);

		parallelTransition.setOnFinished(_ ->
		{
			if(!in)
				root.getChildren().remove(container);
		});
		return parallelTransition;
	}

	public void showProgress(boolean show)
	{
		if(!Platform.isFxApplicationThread())
		{
			Platform.runLater(() -> showProgress(show));
			return;
		}

		if(show)
		{
			if(!root.getChildren().contains(container))
			{
				root.getChildren().add(container);
				inTransition.play();
			}
		}
		else
		{
			outTransition.play();
		}
	}
}
