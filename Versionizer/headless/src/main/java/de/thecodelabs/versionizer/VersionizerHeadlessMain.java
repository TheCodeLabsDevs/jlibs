package de.thecodelabs.versionizer;

import com.google.gson.Gson;
import de.thecodelabs.logger.FileOutputOption;
import de.thecodelabs.logger.Logger;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.service.VersionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VersionizerHeadlessMain
{
	public static void main(String[] args)
	{
		App app = ApplicationUtils.registerMainApplication(VersionizerHeadlessMain.class);
		ApplicationUtils.addAppListener(VersionizerHeadlessMain::applicationWillStart);
		app.start(args);

		if (args.length == 1)
		{
			try
			{
				Files.write(Paths.get("/Users/tobias/Desktop/HEADLESS.txt"), args[0].getBytes());
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}

			Gson gson = new Gson();
			VersionizerItem versionizerItem = gson.fromJson(args[0], VersionizerItem.class);

			VersionService versionService = new VersionService(versionizerItem);

			for (Artifact artifact : versionizerItem.getArtifacts()) {
				Logger.info(artifact);
			}
		}
	}

	public static void applicationWillStart(App app) {
		Logger.init(ApplicationUtils.getApplication().getPath(PathType.LOG));
		Logger.setFileOutput(FileOutputOption.COMBINED);
	}
}
