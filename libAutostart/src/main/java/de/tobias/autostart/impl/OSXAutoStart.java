package de.tobias.autostart.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import de.tobias.autostart.Autostart;

/**
 * Default Autostartimplementation für Mac OS X. Verwaltet LaunchAgents in
 * ~/Library/LaunchAgents.
 * 
 * @author tobias
 * @version 1.0
 * 
 */
public class OSXAutoStart implements Autostart {

	private File root;

	public OSXAutoStart() {
		root = new File(System.getProperty("user.home") + "/Library/LaunchAgents");
	}

	@Override
	public String name() {
		return "os x";
	}

	/**
	 * Erstellt eine LaunchAgent, indem ein plist Eintrag erstellt wird.
	 */
	@Override
	public void add(String name, File src) throws IOException {
		File output = new File(root, name + ".plist");

		InputStream in = getClass().getClassLoader().getResourceAsStream("libraries/OSXTemplate.plist");

		String value = read(in).replace("{path}", src.getAbsolutePath()).replace("{name}", name)
				.replace("{dir}", src.getParent());
		Files.write(output.toPath(), value.getBytes(), StandardOpenOption.CREATE);
	}

	/**
	 * Überprüft einen plist Eintrag auf die Existenz
	 */
	@Override
	public boolean isAutostart(String name, File src) throws IOException {
		File output = new File(root, name + ".plist");

		if (output.exists())
			return read(output).contains(src.getAbsolutePath());
		return false;
	}

	/**
	 * Löscht eine plist Datei
	 */
	@Override
	public void removeAutostart(String name) {
		File output = new File(root, name + ".plist");
		output.delete();
	}

	private String read(InputStream stream) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		StringBuilder builder = new StringBuilder();

		String tmp;
		while ((tmp = reader.readLine()) != null) {
			builder.append(tmp).append("\n");
		}
		return builder.toString();
	}

	private String read(File file) throws IOException {
		return read(new FileInputStream(file));
	}

}
