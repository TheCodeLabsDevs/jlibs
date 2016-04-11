package de.tobias.utils.application.update;

import java.io.IOException;

import de.tobias.utils.application.App;
import de.tobias.utils.application.container.PathType;
import de.tobias.utils.util.FileUtils;

public class HelpMapUpdateService implements UpdateService {

	@Override
	public void update(App app, long minor, long major) {
		try {
			FileUtils.deleteDirectory(app.getPath(PathType.HELPMAP));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
