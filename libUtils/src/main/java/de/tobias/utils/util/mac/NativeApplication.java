package de.tobias.utils.util.mac;

import de.tobias.utils.util.ImageUtils;
import javafx.scene.image.Image;

import java.nio.file.Path;

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

	public static void setAppearance(boolean darkAqua) {
		setAppearance_N(darkAqua);
	}

	public static void showFileInFileViewer(Path path) {
		showFileInFileViewer_N(path.toString());
	}

	/*
	 Native methods
	 */

	private static native void setDockIcon(byte[] image);

	private static native void setDockIconBadge_N(int i);

	private static native void setDockIconHidden_N(boolean hidden);

	private static native void setAppearance_N(boolean darkAqua);

	private static native void showFileInFileViewer_N(String file);
}
