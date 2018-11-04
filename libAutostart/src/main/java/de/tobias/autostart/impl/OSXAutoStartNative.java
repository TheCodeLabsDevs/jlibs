package de.tobias.autostart.impl;

import java.io.File;

import de.thecodelabs.utils.application.NativeLoader;
import de.tobias.autostart.Autostart;

public class OSXAutoStartNative implements Autostart {

	private boolean loaded = false;

	@Override
	public String name() {
		return "native os x";
	}

	@Override
	public void add(String name, File src) throws Exception {
		load();
		add_N(src.getAbsolutePath());
	}

	@Override
	public boolean isAutostart(String name, File src) throws Exception {
		load();
		return isAutoStart_N(src.getAbsolutePath());
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
