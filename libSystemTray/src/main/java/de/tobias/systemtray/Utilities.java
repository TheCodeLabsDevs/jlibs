package de.tobias.systemtray;

import de.tobias.utils.application.NativeLoader;
import de.tobias.utils.util.ImageUtils;
import javafx.scene.image.Image;

import java.io.IOException;

public class Utilities extends NativeLoader {

	static {
		load("SystemTray", "", Utilities.class, new Thread() {
			@Override
			public void run() {
				SystemTray.startUp_N();
			}
		}, new Thread() {
			@Override
			public void run() {
				SystemTray.tearDown_N();
			}
		});
	}

	private static long id = 0;

	public static long getNewID() {
		return id++;
	}

	public static void setDockIconHidden(boolean hidden) {
		setDockIconHidden_N(hidden);
	}

	public static void setDockIconImage(Image image) throws IOException {
		setDockIconImage_N(ImageUtils.imageToByteArray(image));
	}

	public static void setDockIconBadge(int i) {
		setDockIconBadge_N(i);
	}

	private native static void setDockIconHidden_N(boolean hidden);

	private native static void setDockIconImage_N(byte[] image);

	private native static void setDockIconBadge_N(int i);
}
