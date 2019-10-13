package de.thecodelabs.artifactory;

public class Main
{
	public static void main(String[] args)
	{
		Artifactory artifactory = new Artifactory("https://maven.thecodedev.de/artifactory");
		final MavenRepository repository = artifactory.getRepository("TheCodeLabs-release");
		final Folder folder = repository.getFolder("de/thecodelabs/jlibs");

		System.out.println(folder);
	}
}
