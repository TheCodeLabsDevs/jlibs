package de.tobias.logger;

public class ConsoleUtils {

	public static boolean runningInIntellij() {
		String classPath = System.getProperty("java.class.path");
		return classPath.contains("idea_rt.jar");
	}
}
