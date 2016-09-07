package de.tobias.utils.application;

import java.net.URISyntaxException;
import java.nio.file.Path;

import com.sun.jna.WString;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.Kernel32Util;

import de.tobias.utils.util.OS;
import de.tobias.utils.util.OS.OSType;
import de.tobias.utils.util.win.Shell32X;
import de.tobias.utils.util.SystemUtils;

/**
 * Launches a windows exe as admin
 * 
 * @author tobias
 *
 */
public class NativeLauncher {

	public static void relaunchAsAdmin(String args) throws URISyntaxException {
		if (OS.getType() == OSType.Windows) {
			Path path = SystemUtils.getRunPath();
			executeAsAdministrator(path.toString(), args);
			System.exit(0);
		}
	}

	// http://stackoverflow.com/questions/11041509/elevating-a-processbuilder-process-via-uac
	public static void executeAsAdministrator(String command, String args) {
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
}
