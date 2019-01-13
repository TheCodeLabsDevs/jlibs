package de.thecodelabs.utils.util.win;

import java.nio.CharBuffer;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.ptr.LongByReference;

public interface Kernel32 extends Library {

	Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);
	Kernel32 SYNC_INSTANCE = (Kernel32) Native.synchronizedLibrary(INSTANCE);

	int GetShortPathNameW(WString lpszLongPath, char[] lpdzShortPath, int cchBuffer);

	int GetWindowsDirectoryW(char[] lpdzShortPath, int uSize);

	boolean GetVolumeInformationW(char[] lpRootPathName, CharBuffer lpVolumeNameBuffer, int nVolumeNameSize,
			LongByReference lpVolumeSerialNumber, LongByReference lpMaximumComponentLength, LongByReference lpFileSystemFlags,
			CharBuffer lpFileSystemNameBuffer, int nFileSystemNameSize);

	int SetThreadExecutionState(int EXECUTION_STATE);

	int ES_DISPLAY_REQUIRED = 0x00000002;
	int ES_SYSTEM_REQUIRED = 0x00000001;
	int ES_CONTINUOUS = 0x80000000;
}
