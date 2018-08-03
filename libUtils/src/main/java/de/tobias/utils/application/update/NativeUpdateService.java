package de.tobias.utils.application.update;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.tobias.utils.application.App;
import de.tobias.utils.application.container.PathType;
import de.tobias.utils.util.FileUtils;

public class NativeUpdateService implements UpdateService {

	@Override
	public void update(App app, long minor, long major) {
		try {
			Path path = app.getPath(PathType.NATIVE_LIBRARY);
			if (Files.exists(path))
				FileUtils.deleteFilesInDirectory(path);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
