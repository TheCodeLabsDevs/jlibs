package de.tobias.utils.application.system.impl;

import de.tobias.utils.application.NativeLoader;
import de.tobias.utils.application.system.NativeApplication;
import de.tobias.utils.application.system.NativeFeatureNotSupported;
import de.tobias.utils.io.IOUtils;
import de.tobias.utils.util.ImageUtils;
import de.tobias.utils.util.OS;
import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Path;

public class MacNativeApplication extends NativeApplication {

	private static boolean loaded = false;

	public static void loadNativeLibrary() {
		if (!loaded && OS.isMacOS()) {
			try {
				Path path = NativeLoader.copy("libUtilsNative.dylib", "libraries", MacNativeApplication.class);
				System.load(path.toString());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			loaded = !loaded;
		}
	}

	public MacNativeApplication() {
		loadNativeLibrary();
	}

	@Override
	@NativeFeatureNotSupported
	public void executeAsAdministrator(String command, String args) {

	}

	@Override
	@NativeFeatureNotSupported
	public boolean isTouchInputAvailable() {
		return false;
	}

	@Override
	public void setDockIcon(Image image) {
		setDockIcon_N(ImageUtils.imageToByteArray(image));
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

	@Override
	public Image getImageForFile(Path file) {
		return new Image(IOUtils.byteArrayToInputStream(getImageForFile_N(file.toString())));
	}

	/*
	 Native methods
	 */

	private static native void setDockIcon_N(byte[] image);

	private static native void setDockIconBadge_N(int i);

	private static native void setDockIconHidden_N(boolean hidden);

	private static native void setAppearance_N(boolean darkAqua);

	private static native void showFileInFileViewer_N(String file);

	private static native byte[] getImageForFile_N(String path);
}
