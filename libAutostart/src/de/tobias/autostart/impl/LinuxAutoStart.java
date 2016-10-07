package de.tobias.autostart.impl;

import java.nio.file.Path;

import de.tobias.autostart.Autostart;

// TODO Implement Linux Autostart
public class LinuxAutoStart implements Autostart {
	
	@Override
	public String name() {
		return "linux";
	}

	@Override
	public void add(String name, Path src) throws Exception {
	}
	
	@Override
	public boolean isAutostart(String name, Path src) throws Exception {
		return false;
	}
	
	@Override
	public void removeAutostart(String name) throws Exception {
	}

}
