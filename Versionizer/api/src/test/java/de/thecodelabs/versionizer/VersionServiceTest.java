package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.VersionService;
import de.tobias.logger.Logger;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class VersionServiceTest
{
	public static void main(String[] args)
	{
		Logger.init(Paths.get("./"));

		VersionizerItem versionizerItem = new VersionizerItem();
		versionizerItem.setArtifactoryUrl("https://maven.thecodelabs.de/artifactory");
		versionizerItem.setGroupId("de/tobias");
		versionizerItem.setArtifactId("libMidi");
		versionizerItem.setReleaseRepository("TheCodeLabs-release");
		versionizerItem.setSnapshotRepository("TheCodeLabs-snapshots");

		VersionService versionService = new VersionService();

		Version[] versions = versionService.getVersions(versionizerItem);
		Arrays.sort(versions);

		for(Version version : versions)
		{
			System.out.println(version);

			final List<RemoteFile> remoteFiles = versionService.listFilesForVersion(versionizerItem, version);
			for(RemoteFile remoteFile : remoteFiles)
			{
				System.out.println("\t\t" + remoteFile);
			}
		}
	}
}
