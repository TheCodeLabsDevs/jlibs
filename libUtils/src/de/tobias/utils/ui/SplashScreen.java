package de.tobias.utils.ui;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import de.tobias.utils.application.ApplicationUtils;

public class SplashScreen {

	private Stage stage;
	private Image image;
	
	public SplashScreen() {
		image = new Image(ApplicationUtils.getApplication().getInfo().getSlashScreenImage());
		
		ImageView imageView = new ImageView(image);
		imageView.setFitWidth(200);
		imageView.setFitHeight(200);
		
		Pane pane = new Pane(imageView);
		Scene scene = new Scene(pane);
		stage = new Stage();
		stage.setScene(scene);
		stage.initStyle(StageStyle.UNDECORATED);
		stage.centerOnScreen();
		stage.show();
	}
	
	public Stage getStage() {
		return stage;
	}
}
