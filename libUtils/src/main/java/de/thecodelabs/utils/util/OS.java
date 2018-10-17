package de.thecodelabs.utils.util;

public class OS {

	public enum OSType {
		Windows, MacOSX, Linux, Other
	}

	public enum OSArch {
		x86, x64;
	}

	private static OSType type;
	private static OSArch arch;

	static {
		String arch = System.getProperty("os.arch");

		if (System.getProperty("os.name").contains("Windows")) {
			type = OSType.Windows;
			if (arch.contains("x86")) {
				OS.arch = OSArch.x86;
			} else {
				OS.arch = OSArch.x64;
			}
		} else if (System.getProperty("os.name").contains("OS X")) {
			type = OSType.MacOSX;
			OS.arch = OSArch.x64;
		} else if (System.getProperty("os.name").contains("Linux")) {
			type = OSType.Linux;
			if (arch.contains("x86")) {
				OS.arch = OSArch.x86;
			} else {
				OS.arch = OSArch.x64;
			}
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

	public static OSArch getArch() {
		return arch;
	}
}
