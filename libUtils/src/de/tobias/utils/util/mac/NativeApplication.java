package de.tobias.utils.util.mac;

import de.tobias.utils.util.ImageUtils;
import javafx.scene.image.Image;

public class NativeApplication {

	public static void setDockIcon(Image image) {
		setDockIcon(ImageUtils.imageToByteArray(image));
	}
	public static void setDockIconBadge(int i) {
		setDockIconBadge_N(i);
	}
	public static void setDockIconHidden(boolean hidden) {
		setDockIconHidden_N(hidden);
	}

	private static native void setDockIcon(byte[] image);
	private static native void setDockIconBadge_N(int i);
	private static native void setDockIconHidden_N(boolean hidden);
}