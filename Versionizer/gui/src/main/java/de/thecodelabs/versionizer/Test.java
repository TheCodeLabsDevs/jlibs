package de.thecodelabs.versionizer;

import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.versionizer.config.Artifact;

public class Test
{
	public static void main(String[] args)
	{
		Artifact build = ApplicationUtils.getApplication().getClasspathResource("build.properties").deserialize(StorageTypes.PROPERTIES, Artifact.class);
		System.out.println(build);
	}
}
