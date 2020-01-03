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
	private String resourceFile;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject mavenProject;

	public void execute() throws MojoExecutionException
	{
		final List resources = mavenProject.getBuild().getResources();
		try
		{
			for(Object obj : resources)
			{
				if(obj instanceof Resource)
				{
					Resource resource = (Resource) obj;
					final String directory = resource.getDirectory();
					File srcFile = new File(directory, resourceFile);
					File desFile = new File(mavenProject.getBuild().getOutputDirectory(), resourceFile);

					Map<Object, Object> replacement = new HashMap<Object, Object>();

					replacement.put("pom.artifactId", mavenProject.getArtifactId());
					replacement.put("pom.groupId", mavenProject.getGroupId());
					replacement.put("pom.version", mavenProject.getVersion());

					final Properties properties = mavenProject.getProperties();
					for (Object key : properties.keySet()) {
						replacement.put(key, properties.get(key));
					}

					replaceFile(srcFile, desFile, replacement);
				}
			}
		}
		catch(IOException e)
		{
			throw new MojoExecutionException("IOException", e);
		}
	}

	private void replaceFile(File srcFile, File desFile, Map<Object, Object> replacement) throws IOException
	{
		if (!srcFile.exists()) {
			return;
		}
		BufferedReader reader = new BufferedReader(new FileReader(srcFile));

		StringBuilder content = new StringBuilder();
		String st;
		while((st = reader.readLine()) != null)
		{
			content.append(st).append("\n");
		}
		reader.close();

		StrSubstitutor sub = new StrSubstitutor(replacement);
		String resolvedString = sub.replace(content);

		PrintWriter writer = new PrintWriter(new FileOutputStream(desFile), true);
		writer.append(resolvedString);
		writer.close();
	}
}
