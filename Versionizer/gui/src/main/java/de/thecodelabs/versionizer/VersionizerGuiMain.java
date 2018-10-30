package de.thecodelabs.versionizer;

import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import javafx.application.Application;
import javafx.stage.Stage;

public class VersionizerGuiMain extends Application
{
	public static void main(String[] args)
	{
		App app = ApplicationUtils.registerMainApplication(VersionizerGuiMain.class);
		app.start(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{

	}
}
