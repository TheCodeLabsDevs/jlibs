package de.thecodelabs.versionizer;

import de.thecodelabs.logger.Logger;
import de.thecodelabs.versionizer.config.Artifact;
import de.thecodelabs.versionizer.config.Repository;
import de.thecodelabs.versionizer.model.RemoteFile;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.UpdateService;
import de.thecodelabs.versionizer.service.VersionService;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class VersionServiceTest
{
	public static void main(String[] args)
	{
		Logger.init(Paths.get("./"));

		final Repository repository = new Repository();
		final Artifact build = new Artifact();
		repository.setUrl("https://maven.thecodelabs.de/artifactory");
		build.setGroupId("de.thecodelabs.versionizer");
		build.setArtifactId("gui");
		repository.setRepositoryNameReleases("TheCodeLabs-release");
		repository.setRepositoryNameSnapshots("TheCodeLabs-snapshots");

		VersionService versionService = new VersionService(repository, UpdateService.RepositoryType.RELEASE);

		List<Version> versions = versionService.getVersionsSorted(build);

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
