package de.tobias.utils.application.system;

import de.tobias.utils.application.system.impl.MacNativeApplication;
import de.tobias.utils.util.OS;
import javafx.scene.image.Image;

import java.lang.reflect.Method;
import java.nio.file.Path;

public abstract class NativeApplication {

	private static NativeApplication shared;

	public static NativeApplication sharedInstance() {
		if (shared == null) {
			switch (OS.getType()) {
				case Windows:
					shared = null;
					break;
				case MacOSX:
					shared = new MacNativeApplication();
					break;
				default:
					shared = null;
					break;
			}
		}
		return shared;
	}

	public boolean isFeatureSupported(NativeFeature nativeFeature) {
		for (Method method : shared.getClass().getDeclaredMethods()) {
			if (method.getName().equals(nativeFeature.getMethodName())) {
				if (method.isAnnotationPresent(NativeFeatureNotSupported.class)) {
					return false;
				}
			}
		}
		return true;
	}

	// Awake

	// Run as Admin

	// Explorer/Finder

	// isTouch

	public abstract void setDockIcon(Image image);

	public abstract void setDockIconBadge(int i);

	public abstract void setDockIconHidden(boolean hidden);

	public abstract void setAppearance(boolean darkAqua);

	public abstract void showFileInFileViewer(Path path);
}
