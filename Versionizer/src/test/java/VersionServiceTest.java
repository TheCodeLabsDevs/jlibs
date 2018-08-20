import de.tobias.versionizer.VersionizerItem;
import de.tobias.versionizer.model.Version;
import de.tobias.versionizer.service.VersionService;

import java.util.Arrays;

public class VersionServiceTest
{
	public static void main(String[] args)
	{
		VersionizerItem versionizerItem = new VersionizerItem();
		versionizerItem.setArtifactoryUrl("https://maven.thecodelabs.de/artifactory");
		versionizerItem.setGroupId("de/tobias");
		versionizerItem.setArtifactId("libUtils");
		versionizerItem.setReleaseRepository("TheCodeLabs-release");
		versionizerItem.setSnapshotRepository("TheCodeLabs-snapshots");

		VersionService versionService = new VersionService();

		Version[] versions = versionService.getVersions(versionizerItem);
		Arrays.sort(versions);

		System.out.println(Arrays.toString(versions));
	}
}
