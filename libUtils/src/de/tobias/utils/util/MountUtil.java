package de.tobias.utils.util;

import java.io.IOException;

public class MountUtil {

	public static void mount(String source, String desitation) throws IOException {
		Runtime.getRuntime().exec(new String[] { "mount", source, desitation });
	}

	public static void umount(String source) throws IOException {
		Runtime.getRuntime().exec(new String[] { "umount", source });
	}
}
