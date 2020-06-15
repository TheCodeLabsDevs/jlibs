package de.thecodelabs.artifactory;

public class Main
{
	public static void main(String[] args)
	{
		Artifactory artifactory = new Artifactory("https://maven.thecodelabs.de/artifactory");
		final MavenRepository repository = artifactory.getRepository("TheCodeLabs-release");
		final Folder folder = repository.getFolder("de/thecodelabs/jlibs");
		final File file = repository.getFile("de/thecodelabs/libUtils/3.0.0/libUtils-3.0.0.jar");

		System.out.println(folder);
		System.out.println(file);
	}
}
