package de.tobias.utils;

import de.tobias.utils.application.system.NativeApplication;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NativeApplicationTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		final Path file = Paths.get("D:/Exploror_Error.png");
		Image image = NativeApplication.sharedInstance().getImageForFile(file);
		ImageView imageView = new ImageView(image);
		primaryStage.setScene(new Scene(new VBox(imageView)));
		primaryStage.show();
	}
}
