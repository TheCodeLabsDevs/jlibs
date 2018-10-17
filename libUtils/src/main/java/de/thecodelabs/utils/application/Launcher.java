package de.thecodelabs.utils.application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import de.thecodelabs.utils.util.OS;
import de.thecodelabs.utils.application.container.PathType;

public class Launcher {

	public static void launchApplication(App app, String[] args) throws InterruptedException, IOException, URISyntaxException {
		List<String> commands = new ArrayList<>();
		Path path = Paths.get(Launcher.class.getProtectionDomain().getCodeSource().getLocation().toURI());

		// Java Command
		commands.add(System.getProperty("java.home") + "/bin/java");

		// Library
		commands.add("-classpath");
		commands.add(path.toString());

		// Native
		copyNative(path.toFile(), app);
		commands.add("-Djava.library.path=" + app.getPath(PathType.NATIVE_LIBRARY));

		// Main
		commands.add(app.getInfo().getSubMain());

		// Main Args
		Collections.addAll(commands, args);

		ProcessBuilder processBuilder = new ProcessBuilder(commands);
		Process process = processBuilder.start();

		writeConsoleOutput(process);

		process.waitFor();
	}

	public static void copyNative(File file, App app) throws IOException {
		JarFile jarFile = new JarFile(file, false);
		Enumeration<JarEntry> entities = jarFile.entries();

		while (entities.hasMoreElements()) {
			JarEntry entry = entities.nextElement();

			if ((!entry.isDirectory()) && (entry.getName().indexOf('/') == -1)) {
				if (isNativeFile(entry.getName())) {
					InputStream in = jarFile.getInputStream(jarFile.getEntry(entry.getName()));
					Path outputPath = app.getPath(PathType.NATIVE_LIBRARY, entry.getName());
					if (Files.notExists(outputPath)) {
						Files.createDirectories(outputPath.getParent());
						Files.createFile(outputPath);
					}
					OutputStream out = new FileOutputStream(outputPath.toString());

					byte[] buffer = new byte[65536];
					int bufferSize;
					while ((bufferSize = in.read(buffer, 0, buffer.length)) != -1) {
						out.write(buffer, 0, bufferSize);
					}

					in.close();
					out.close();
				}
			}
		}
		jarFile.close();
	}

	private static boolean isNativeFile(String entryName) {
		String name = entryName.toLowerCase();

		if (OS.isWindows()) {
			return name.endsWith(".dll");
		} else if (OS.isLinux()) {
			return name.endsWith(".so");
		} else return (OS.isMacOS())
				&& ((name.endsWith(".jnilib")) || (name.endsWith(".dylib")));

	}

	private static void writeConsoleOutput(Process process) throws IOException {
		InputStream is = process.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
}
