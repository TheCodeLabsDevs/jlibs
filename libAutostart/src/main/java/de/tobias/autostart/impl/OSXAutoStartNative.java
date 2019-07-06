package de.tobias.autostart.impl;

import de.thecodelabs.utils.application.NativeLoader;
import de.tobias.autostart.Autostart;

import java.nio.file.Path;

public class OSXAutoStartNative implements Autostart {

	private boolean loaded = false;

	@Override
	public String name() {
		return "MacOSNative";
	}

	@Override
	public void add(String name, Path src) throws Exception {
		load();
		add_N(src.toAbsolutePath().toString());
	}

	@Override
	public boolean isAutostart(String name, Path src) throws Exception {
		load();
		return isAutoStart_N(src.toAbsolutePath().toString());
	}

	@Override
	public void removeAutostart(String name) throws Exception {
		load();
		removeAutoStart_N(name);
	}
	
	private void load() {
		if (!loaded) {
			NativeLoader.load("AutoStart", "libraries/", OSXAutoStartNative.class);
			loaded = !loaded;
		}
	}

	private native void add_N(String src);

	private native boolean isAutoStart_N(String src);

	private native void removeAutoStart_N(String name);

}
