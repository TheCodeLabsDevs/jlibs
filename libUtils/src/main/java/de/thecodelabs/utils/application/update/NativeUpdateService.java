package de.thecodelabs.utils.application.update;

import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
