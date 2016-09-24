package de.tobias.updater.main;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import de.tobias.updater.viewcontroller.UpdateViewController;
import de.tobias.utils.util.StringUtils;
import javafx.application.Application;
import javafx.stage.Stage;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

public class UpdaterMain extends Application {

	// Parameters: "{downloadPath:path, files:[{url:remotepath, local:path}, {...}],executePath:path}"
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		String parameter = StringUtils.build(getParameters().getRaw(), " ");
		JSONObject data = (JSONObject) new JSONParser(JSONParser.ACCEPT_NON_QUOTE).parse(parameter);
		Path downloadPath = Paths.get((String) data.get("downloadPath"));

		List<UpdateFile> updateFiles = new ArrayList<>();

		JSONArray files = (JSONArray) data.get("files");
		for (Object jsonObject : files) {
			JSONObject jsonObject2 = (JSONObject) jsonObject;
			UpdateFile file = new UpdateFile(new URL((String) jsonObject2.get("url")), Paths.get((String) jsonObject2.get("local")));
			updateFiles.add(file);
		}

		Path executePath = null;
		if (data.containsKey("executePath")) {
			executePath = Paths.get((String) data.get("executePath"));
		}

		UpdateViewController controller = new UpdateViewController(downloadPath, updateFiles, executePath);
		controller.getStage().show();
	}
}
