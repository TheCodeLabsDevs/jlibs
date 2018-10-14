import de.thecodelabs.versionizer.VersionizerItem;
import de.thecodelabs.versionizer.model.Version;
import de.thecodelabs.versionizer.service.VersionService;

import java.util.Arrays;

public class VersionServiceTest
{
	public static void main(String[] args)
	{
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
			if(version.isSnapshot())
			{
				versionService.listFilesForVersion(versionizerItem, version);
				System.out.println();
			}
		}
	}
}
