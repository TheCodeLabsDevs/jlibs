package de.tobias.utils.util;

public class OS {

	public enum OSType {
		Windows, MacOSX, Linux, Other
	}

	private static OSType type;

	static {
		if (System.getProperty("os.name").contains("Windows")) {
			type = OSType.Windows;
		} else if (System.getProperty("os.name").contains("OS X")) {
			type = OSType.MacOSX;
		} else if (System.getProperty("os.name").contains("Linux")) {
			type = OSType.Linux;
		} else {
			type = OSType.Other;
		}
	}
	
	public static boolean isWindows() {
		return type == OSType.Windows;
	}
	
	public static boolean isMacOS() {
		return type == OSType.MacOSX;
	}
	
	public static boolean isLinux() {
		return type == OSType.Linux;
	}

	public static OSType getType() {
		return type;
	}
}
