package de.tobias.utils.application.system.impl;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;
import de.tobias.utils.application.NativeLoader;
import de.tobias.utils.application.system.NativeApplication;
import de.tobias.utils.application.system.NativeFeatureNotSupported;
import de.tobias.utils.io.IOUtils;
import de.tobias.utils.util.OS;
import de.tobias.utils.util.win.Shell32X;
import de.tobias.utils.util.win.User32X;
import javafx.scene.image.Image;

import java.io.IOException;
import java.nio.file.Path;

public class WindowsNativeApplication extends NativeApplication {

	private static boolean loaded = false;

	private static void loadNativeLibrary() {
		if (!loaded && OS.isWindows()) {
			NativeLoader.load("SystemUtilsWindows.dll", "libraries", WindowsNativeApplication.class);
			loaded = !loaded;
		}
	}

	public WindowsNativeApplication() {
		loadNativeLibrary();
	}

	@Override
	public void preventSystemSleep(boolean on) {
		if (on) {
			de.tobias.utils.util.win.Kernel32.INSTANCE.SetThreadExecutionState(de.tobias.utils.util.win.Kernel32.ES_CONTINUOUS | de.tobias.utils.util.win.Kernel32.ES_DISPLAY_REQUIRED | de.tobias.utils.util.win.Kernel32.ES_SYSTEM_REQUIRED);
		} else {
			de.tobias.utils.util.win.Kernel32.INSTANCE.SetThreadExecutionState(de.tobias.utils.util.win.Kernel32.ES_CONTINUOUS);
		}
	}

	// http://stackoverflow.com/questions/11041509/elevating-a-processbuilder-process-via-uac
	@Override
	public void executeAsAdministrator(String command, String args) {
		Shell32X.SHELLEXECUTEINFO execInfo = new Shell32X.SHELLEXECUTEINFO();
		execInfo.lpFile = new WString(command);
		if (args != null)
			execInfo.lpParameters = new WString(args);
		execInfo.nShow = Shell32X.SW_SHOWDEFAULT;
		execInfo.fMask = Shell32X.SEE_MASK_NOCLOSEPROCESS;
		execInfo.lpVerb = new WString("runas");
		boolean result = Shell32X.INSTANCE.ShellExecuteEx(execInfo);

		if (!result) {
			int lastError = Kernel32.INSTANCE.GetLastError();
			String errorMessage = Kernel32Util.formatMessageFromLastErrorCode(lastError);
			throw new RuntimeException(
					"Error performing elevation: " + lastError + ": " + errorMessage + " (apperror=" + execInfo.hInstApp + ")");
		}
	}

	@Override
	public boolean isTouchInputAvailable() {
		return User32X.isTouchAvailable();
	}

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

	@Override
	public Image getImageForFile(Path file) {
		return new Image(IOUtils.byteArrayToInputStream(getImageForFile_N(file.toString())));
	}

	private static native byte[] getImageForFile_N(String path);

}
