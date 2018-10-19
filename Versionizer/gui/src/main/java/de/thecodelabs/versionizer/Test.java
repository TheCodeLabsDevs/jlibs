package de.thecodelabs.versionizer;

import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.versionizer.config.Build;

public class Test
{
	public static void main(String[] args)
	{
		Build build = ApplicationUtils.getApplication().getClasspathResource("build.properties").deserialize(StorageTypes.PROPERTIES, Build.class);
		System.out.println(build);
	}
}
