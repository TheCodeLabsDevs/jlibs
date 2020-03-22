package de.thecodelabs.utils;

import de.thecodelabs.utils.application.system.NativeApplication;
import de.thecodelabs.utils.threading.Worker;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class NativeApplicationTest extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		Worker.runLater(() -> {
			try
			{
				Thread.sleep(10000);
				System.out.println("Start");
				Platform.runLater(() -> NativeApplication.sharedInstance().requestUserAttentionByStage(primaryStage));
				Thread.sleep(10000);
				System.out.println("Stop");
				Platform.runLater(() -> NativeApplication.sharedInstance().cancelUserAttentionByStage(primaryStage));
			}
			catch(InterruptedException e)
			{

			}
		});

		primaryStage.setScene(new Scene(new Label("2")));
		primaryStage.show();
	}
}
