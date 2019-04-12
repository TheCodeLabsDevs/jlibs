package de.thecodelabs.swagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

@Mojo(name = "documentation-convert", defaultPhase = LifecyclePhase.COMPILE)
public class DocumentationConvertMojo extends AbstractMojo
{

	@Parameter(property = "directory")
	private String directory;

	@Parameter(property = "specs")
	private String specs;

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject mavenProject;

	public void execute() throws MojoExecutionException, MojoFailureException
	{
		try
		{
			getLog().info("Start swagger Plugin");
			File srcFile = new File(directory, specs + ".yaml");
			getLog().info("Read specs file " + srcFile.getAbsolutePath());

			if(srcFile.exists())
			{
				File desFile = new File(mavenProject.getBuild().getDirectory(), specs + ".html");
				desFile.getParentFile().mkdirs();

				final InputStream template = getClass().getClassLoader().getResourceAsStream("specs-template.html");

				Map<String, Object> replacement = new HashMap<>();
				replacement.put("title", mavenProject.getArtifactId());
				replacement.put("specs", convertYamlToJson(srcFile));

				ExpressionParser parser = new SpelExpressionParser();
				StandardEvaluationContext context = new StandardEvaluationContext();
				context.setVariables(replacement);

				if(template != null)
				{
					String editContent = parser.parseExpression(IOUtil.toString(template), new TemplateParserContext("${", "}"))
							.getValue(context, String.class);

					if(editContent != null)
					{
						Files.write(desFile.toPath(), editContent.getBytes());
						getLog().info("Write html to" + desFile.getAbsolutePath());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String convertYamlToJson(File yaml) throws IOException
	{
		ObjectMapper yamlReader = new ObjectMapper(new YAMLFactory());
		Object obj = yamlReader.readValue(yaml, Object.class);

		ObjectMapper jsonWriter = new ObjectMapper();
		return jsonWriter.writeValueAsString(obj);
	}
}
