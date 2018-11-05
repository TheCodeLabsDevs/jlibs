package de.thecodelabs;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * @phase process-sources
 */
@Mojo(name = "resource-fill")
public class ResourceFillMojo extends AbstractMojo
{
	/**
	 * Location of the file.
	 *
	 * @required
	 */
	@Parameter(property = "resourceFile")
	private File resourceFile;

	@Parameter(defaultValue="${project}", readonly=true, required=true)
	private MavenProject mavenProject;

	public void execute() throws MojoExecutionException
	{

	}
}
