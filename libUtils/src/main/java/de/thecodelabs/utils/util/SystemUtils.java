package de.thecodelabs.utils.util;

import de.thecodelabs.utils.logger.LoggerBridge;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemUtils
{

	public static Path getApplicationSupportDirectoryPath(String name)
	{
		switch(OS.getType())
		{
			case Windows:
				return Paths.get(System.getenv("APPDATA"), name);
			case MacOSX:
				return Paths.get(System.getProperty("user.home"), "Library/Application Support/", name);
			case Linux:
				return Paths.get(System.getProperty("user.home"), "." + name);
			default:
				return null;
		}
	}

	public static Path getApplicationSupportDirectoryPath(String... name)
	{
		return getApplicationSupportDirectoryPath(StringUtils.build(name, File.separator));
	}

	public static Path getRunPath()
	{
		try
		{
			return Paths.get(SystemUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		}
		catch(URISyntaxException | FileSystemNotFoundException e)
		{
			LoggerBridge.debug(e.getMessage());
			return null;
		}
	}

	public static boolean isJar()
	{
		final Path runPath = getRunPath();
		if(runPath != null)
		{
			return runPath.toString().toLowerCase().endsWith(".jar");
		}
		else
		{
			return false;
		}
	}

	public static boolean isExe()
	{
		final Path runPath = getRunPath();
		if(runPath != null)
		{
			return runPath.toString().toLowerCase().endsWith(".exe");
		}
		else
		{
			return false;
		}
	}

	public static boolean isDocker()
	{
		return Files.exists(Paths.get("/.dockerenv"));
	}

	// Linux only
	public static boolean isRootUser()
	{
		return System.getenv().get("USER").equalsIgnoreCase("root");
	}

}
