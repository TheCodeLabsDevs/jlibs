package de.thecodelabs;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

@Mojo(name = "resource-fill")
public class ResourceFillMojo extends AbstractMojo
{
	@Parameter(property = "resourceFile")
	private File resourceFile;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject mavenProject;

	public void execute() throws MojoExecutionException
	{

	}
}
