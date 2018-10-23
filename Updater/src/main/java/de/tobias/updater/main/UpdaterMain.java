package de.tobias.updater.main;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.thecodelabs.utils.ui.Alerts;
import de.thecodelabs.utils.util.StringUtils;
import de.tobias.updater.viewcontroller.UpdateViewController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class UpdaterMain extends Application {

	// Parameters: "{downloadPath:path, files:[{url:remotepath, local:path}, {...}],executePath:path}"
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String parameter = StringUtils.build(getParameters().getRaw(), " ");
		if (parameter.isEmpty()) {
			Alert alert = Alerts.getInstance().createAlert(Alert.AlertType.ERROR, "Initialize Error", "No Parameter defined");
			alert.showAndWait();
			Platform.exit();
			return;
		}

		JsonObject data = (JsonObject) new JsonParser().parse(parameter);
		Path downloadPath = Paths.get(data.get("downloadPath").getAsString());

		List<UpdateFile> updateFiles = new ArrayList<>();

		JsonArray files = data.getAsJsonArray("files");
		for (JsonElement obj : files) {
			JsonObject jsonObject = obj.getAsJsonObject();
			UpdateFile file = new UpdateFile(new URL(jsonObject.get("url").getAsString()), jsonObject.get("local").getAsString());
			updateFiles.add(file);
		}

		Path executePath = null;
		if (data.get("executePath") != null) {
			executePath = Paths.get(data.get("executePath").getAsString());
		}

		UpdateViewController controller = new UpdateViewController(downloadPath, updateFiles, executePath);
		controller.showStage();
	}
}
