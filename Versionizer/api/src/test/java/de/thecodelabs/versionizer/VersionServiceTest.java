package de.thecodelabs.versionizer;

import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.VersionService;
import de.thecodelabs.logger.Logger;

import java.io.IOException;
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

		VersionService versionService = new VersionService(versionizerItem);

		List<Version> versions = versionService.getVersions();

		for(Version version : versions)
		{
			System.out.println(version);

			final List<RemoteFile> remoteFiles = versionService.listFilesForVersion(version);
			for(RemoteFile remoteFile : remoteFiles)
			{
				System.out.println("\t\t" + remoteFile);
			}
		}

		final Version version = versions.get(versions.size() - 1);
		RemoteFile file = versionService.listFilesForVersion(version).get(0);
		try
		{
			versionService.downloadRemoteFile(file, Paths.get("download.jar"));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
