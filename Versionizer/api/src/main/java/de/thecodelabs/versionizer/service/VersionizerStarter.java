package de.thecodelabs.versionizer.service;

import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.App;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.application.external.ExternalClasspathResourceContainer;
import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.config.Build;
import de.thecodelabs.versionizer.config.Repository;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

public class VersionizerStarter
{
	public static void startVersionizer(VersionizerItem versionizerItem)
	{

	}

	private boolean checkVersionizerUpdate() {
		return false;
	}

	private void downloadVersion() {
		App app = ApplicationUtils.getApplication();
		final Path versionizerPath = app.getPath(PathType.RESOURCES, "");

		if (Files.exists(versionizerPath))
		{
			ExternalClasspathResourceContainer container = ExternalClasspathResourceContainer.getExternalClasspath(versionizerPath);
			Repository repository = container.get("repository.yml").deserialize(StorageTypes.YAML, Repository.class);
			Build build = container.get("build.properties").deserialize(StorageTypes.PROPERTIES, Build.class);

			VersionizerItem versionizerItem = new VersionizerItem(repository, Collections.singletonList(build), versionizerPath.toString());

			container.close();
		}
	}
}
