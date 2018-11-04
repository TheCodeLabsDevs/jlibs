package de.thecodelabs.utils;

import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.system.NativeApplication;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NativeApplicationTest extends Application
{
	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		final Path file = Paths.get("/");
		final Path file1 = file.toAbsolutePath();
		System.out.println(file1);

		Image image = NativeApplication.sharedInstance().getImageForFile(file1);
		ImageView imageView = new ImageView(image);
		primaryStage.setScene(new Scene(new VBox(imageView)));
		primaryStage.show();
	}
}
