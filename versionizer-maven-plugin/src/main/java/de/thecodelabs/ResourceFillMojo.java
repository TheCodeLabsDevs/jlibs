package de.thecodelabs;

import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Mojo(name = "resource-fill")
public class ResourceFillMojo extends AbstractMojo
{
	@Parameter(property = "resourceFile")
	private List<String> resourceFiles;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject mavenProject;

	public void execute() throws MojoExecutionException
	{
		final List<?> resources = mavenProject.getBuild().getResources();
		for(Object obj : resources)
		{
			if(obj instanceof Resource)
			{
				Resource resource = (Resource) obj;
				final String directory = resource.getDirectory();
				for(String resourceFile : resourceFiles)
				{
					try
					{
						prepareFile(directory, resourceFile);
					}
					catch(IOException e)
					{
						throw new MojoExecutionException("IOException", e);
					}
				}
			}
		}
	}

	public void prepareFile(String directory, String resourceFile) throws IOException
	{
		File srcFile = new File(directory, resourceFile);
		File desFile = new File(mavenProject.getBuild().getOutputDirectory(), resourceFile);

		Map<Object, Object> replacement = new HashMap<Object, Object>();

		replacement.put("pom.artifactId", mavenProject.getArtifactId());
		replacement.put("pom.groupId", mavenProject.getGroupId());
		replacement.put("pom.version", mavenProject.getVersion());

		final Properties properties = mavenProject.getProperties();
		for(Map.Entry<Object, Object> entry : properties.entrySet())
		{
			replacement.put(entry.getKey(), entry.getValue());
		}

		replaceFile(srcFile, desFile, replacement);
	}

	private void replaceFile(File srcFile, File desFile, Map<Object, Object> replacement) throws IOException
	{
		if(!srcFile.exists())
		{
			return;
		}
		try(BufferedReader reader = new BufferedReader(new FileReader(srcFile));
			PrintWriter writer = new PrintWriter(new FileOutputStream(desFile), true))
		{

			StringBuilder content = new StringBuilder();
			String st;
			while((st = reader.readLine()) != null)
			{
				content.append(st).append("\n");
			}

			StrSubstitutor sub = new StrSubstitutor(replacement);
			String resolvedString = sub.replace(content);

			writer.append(resolvedString);
		}
	}
}
