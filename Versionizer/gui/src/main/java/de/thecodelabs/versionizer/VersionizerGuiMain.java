package de.thecodelabs.versionizer;

import com.google.gson.Gson;
import de.thecodelabs.logger.FileOutputOption;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.versionizer.controller.MainViewController;
import javafx.application.Application;
import javafx.stage.Stage;

public class VersionizerGuiMain extends Application
{
	public static void main(String[] args)
	{
		App app = ApplicationUtils.registerMainApplication(VersionizerGuiMain.class);
		ApplicationUtils.addAppListener(VersionizerGuiMain::applicationWillStart);
		app.start(args);
	}


	private static void applicationWillStart(App app)
	{
		Logger.init(ApplicationUtils.getApplication().getPath(PathType.LOG));
		Logger.setFileOutput(FileOutputOption.COMBINED);
	}

	@Override
	public void start(Stage primaryStage)
	{
		Gson gson = new Gson();
		final String json = getParameters().getRaw().get(0);
		UpdateItem updateItem = gson.fromJson(json, UpdateItem.class);

		MainViewController mainViewController = new MainViewController(primaryStage, updateItem);
		mainViewController.showStage();
	}
}
