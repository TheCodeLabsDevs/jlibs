package de.tobias.utils.application.system.impl;

import de.tobias.utils.application.system.NativeApplication;
import de.tobias.utils.application.system.NativeFeatureNotSupported;
import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Path;

public class WindowsNativeApplication extends NativeApplication {

	@Override
	@NativeFeatureNotSupported
	public void setDockIcon(Image image) {

	}

	@Override
	@NativeFeatureNotSupported
	public void setDockIconBadge(int i) {

	}

	@Override
	@NativeFeatureNotSupported
	public void setDockIconHidden(boolean hidden) {

	}

	@Override
	@NativeFeatureNotSupported
	public void setAppearance(boolean darkAqua) {

	}

	@Override
	public void showFileInFileViewer(Path path) {
		String pathString = path.toString().replace("/", "\\");
		try {
			Runtime.getRuntime().exec(new String[]
					{
							"explorer.exe",
							"/select,",
							"\"" + pathString + "\""
					});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
}
