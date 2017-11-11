package de.tobias.update.main;

import javafx.application.Application;
import javafx.stage.Stage;
import de.tobias.update.viewcontroller.UpdateViewController;

public class UpdateManager extends Application {

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		UpdateViewController controller = new UpdateViewController(stage, getParameters().getNamed().get("app"));
		controller.getStage().show();
	}
}
