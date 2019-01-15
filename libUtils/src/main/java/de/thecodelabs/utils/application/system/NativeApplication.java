package de.thecodelabs.utils.application.system;

import de.thecodelabs.utils.util.OS;
import de.thecodelabs.utils.application.system.impl.MacNativeApplication;
import de.thecodelabs.utils.application.system.impl.WindowsNativeApplication;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.lang.reflect.Method;
import java.nio.file.Path;

public abstract class NativeApplication {

	public enum RequestUserAttentionType {
		CRITICAL_REQUEST,
		INFORMATIONAL_REQUEST
	}

	private static NativeApplication shared;

	public static NativeApplication sharedInstance() {
		if (shared == null) {
			switch (OS.getType()) {
				case Windows:
					shared = new WindowsNativeApplication();
					break;
				case MacOSX:
					shared = new MacNativeApplication();
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
	public abstract void preventSystemSleep(boolean on);

	public abstract void executeAsAdministrator(String command, String args);

	public abstract boolean isTouchInputAvailable();

	public abstract void requestUserAttention(RequestUserAttentionType requestUserAttentionType);

	public abstract void cancelUserAttention();

	public abstract void requestUserAttentionByStage(Stage stage);

	public abstract void cancelUserAttentionByStage(Stage stage);

	public abstract void setDockIcon(Image image);

	public abstract void setDockIconBadge(int i);

	public abstract void setDockIconHidden(boolean hidden);

	public abstract void setAppearance(boolean darkAqua);

	public abstract void showFileInFileViewer(Path path);

	public abstract Image getImageForFile(Path file);
}
