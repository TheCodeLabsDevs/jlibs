package de.thecodelabs.utils.util.win;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.win32.W32APIOptions;

public interface User32X extends User32 {

	User32X INSTANCE = Native.load("user32", User32X.class, W32APIOptions.UNICODE_OPTIONS);

	int GetSystemMetrics(int nIndex);

	static boolean isTouchAvailable() {
		return User32X.INSTANCE.GetSystemMetrics(95) > 0;
	}
}
