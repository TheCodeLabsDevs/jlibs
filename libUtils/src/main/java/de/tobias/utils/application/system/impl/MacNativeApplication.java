package de.tobias.utils.application.system.impl;

import de.tobias.utils.application.system.NativeApplication;
import de.tobias.utils.util.ImageUtils;
import javafx.scene.image.Image;

import java.nio.file.Path;

public class MacNativeApplication extends NativeApplication {

	@Override
	public void setDockIcon(Image image) {
		setDockIcon(ImageUtils.imageToByteArray(image));
	}

	@Override
	public void setDockIconBadge(int i) {
		setDockIconBadge_N(i);
	}

	@Override
	public void setDockIconHidden(boolean hidden) {
		setDockIconHidden_N(hidden);
	}

	@Override
	public void setAppearance(boolean darkAqua) {
		setAppearance_N(darkAqua);
	}

	@Override
	public void showFileInFileViewer(Path path) {
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
