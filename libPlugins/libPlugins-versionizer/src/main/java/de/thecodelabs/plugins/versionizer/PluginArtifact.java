package de.thecodelabs.plugins.versionizer;

import de.thecodelabs.plugins.PluginDescriptor;
import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.versionizer.config.Artifact;

import java.io.InputStream;

public interface PluginArtifact
{
	default Artifact getArtifact()
	{
		final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("plugin.yml");
		final PluginDescriptor plugin = Storage.load(resourceAsStream, StorageTypes.YAML, PluginDescriptor.class);

		final Artifact artifact = new Artifact();
		artifact.setArtifactId(plugin.getArtifactId());
		artifact.setGroupId(plugin.getGroupId());
		artifact.setVersion(plugin.getVersion());
		artifact.setArtifactType(Artifact.ArtifactType.PLUGIN);
		return artifact;
	}
}
