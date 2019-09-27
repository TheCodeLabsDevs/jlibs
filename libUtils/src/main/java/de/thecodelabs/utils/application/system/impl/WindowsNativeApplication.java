package de.thecodelabs.utils.application.system.impl;

import com.sun.javafx.tk.TKStage;
import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.*;
import com.sun.jna.win32.StdCallLibrary;
import de.thecodelabs.utils.application.NativeLoader;
import de.thecodelabs.utils.application.system.NativeApplication;
import de.thecodelabs.utils.application.system.NativeFeatureNotSupported;
import de.thecodelabs.utils.io.IOUtils;
import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.OS;
import de.thecodelabs.utils.util.win.Shell32X;
import de.thecodelabs.utils.util.win.User32X;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Path;

public class WindowsNativeApplication extends NativeApplication
{

	private static boolean loaded = false;

	private static void loadNativeLibrary()
	{
		if(!loaded && OS.isWindows() && OS.getArch() == OS.OSArch.x86)
		{
			NativeLoader.load("SystemUtilsWindows.dll", "libraries", WindowsNativeApplication.class);
			loaded = !loaded;
		}
	}

	public WindowsNativeApplication()
	{
		loadNativeLibrary();
	}

	@Override
	public void preventSystemSleep(boolean on)
	{
		if(on)
		{
			de.thecodelabs.utils.util.win.Kernel32.INSTANCE.SetThreadExecutionState(de.thecodelabs.utils.util.win.Kernel32.ES_CONTINUOUS | de.thecodelabs.utils.util.win.Kernel32.ES_DISPLAY_REQUIRED | de.thecodelabs.utils.util.win.Kernel32.ES_SYSTEM_REQUIRED);
		}
		else
		{
			de.thecodelabs.utils.util.win.Kernel32.INSTANCE.SetThreadExecutionState(de.thecodelabs.utils.util.win.Kernel32.ES_CONTINUOUS);
		}
	}

	// http://stackoverflow.com/questions/11041509/elevating-a-processbuilder-process-via-uac
	@Override
	public void executeAsAdministrator(String command, String args)
	{
		Shell32X.SHELLEXECUTEINFO execInfo = new Shell32X.SHELLEXECUTEINFO();
		execInfo.lpFile = new WString(command);
		if(args != null)
			execInfo.lpParameters = new WString(args);
		execInfo.nShow = Shell32X.SW_SHOWDEFAULT;
		execInfo.fMask = Shell32X.SEE_MASK_NOCLOSEPROCESS;
		execInfo.lpVerb = new WString("runas");
		boolean result = Shell32X.INSTANCE.ShellExecuteEx(execInfo);

		if(!result)
		{
			int lastError = Kernel32.INSTANCE.GetLastError();
			String errorMessage = Kernel32Util.formatMessageFromLastErrorCode(lastError);
			throw new RuntimeException(
					"Error performing elevation: " + lastError + ": " + errorMessage + " (apperror=" + execInfo.hInstApp + ")");
		}
	}

	@Override
	public boolean isTouchInputAvailable()
	{
		return User32X.isTouchAvailable();
	}

	@Override
	@NativeFeatureNotSupported
	public void requestUserAttention(RequestUserAttentionType requestUserAttentionType)
	{

	}

	@Override
	public void requestUserAttentionByStage(Stage stage)
	{
		flashWindow(stage, true, true);
	}

	@Override
	@NativeFeatureNotSupported
	public void cancelUserAttention()
	{

	}

	@Override
	public void cancelUserAttentionByStage(Stage stage)
	{
		flashWindow(stage, false, false);

	}

	@Override
	@NativeFeatureNotSupported
	public void setDockIcon(Image image)
	{

	}

	@Override
	@NativeFeatureNotSupported
	public void setDockIconBadge(int i)
	{

	}

	@Override
	@NativeFeatureNotSupported
	public void setDockIconHidden(boolean hidden)
	{

	}

	@Override
	@NativeFeatureNotSupported
	public void setAppearance(boolean darkAqua)
	{

	}

	@Override
	public void showFileInFileViewer(Path path)
	{
		String pathString = path.toString().replace("/", "\\");
		try
		{
			Runtime.getRuntime().exec(new String[]
					{
							"explorer.exe",
							"/select,",
							"\"" + pathString + "\""
					});
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public Image getImageForFile(Path file)
	{
		if(OS.getArch() == OS.OSArch.x86)
		{
			byte[] bytes = getImageForFile_N(file.toString());
			if(bytes != null)
			{
				return new Image(IOUtils.byteArrayToInputStream(bytes));
			}
			else
			{
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	private static native byte[] getImageForFile_N(String path);

	private static Long getWindowPointer(Stage stage)
	{
		try
		{
			TKStage tkStage = stage.impl_getPeer(); // TODO Work for javajx 8 > javajx 11 has different naming
			Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow");
			getPlatformWindow.setAccessible(true);
			Object platformWindow = getPlatformWindow.invoke(tkStage);
			Method getNativeHandle = platformWindow.getClass().getMethod("getNativeHandle");
			getNativeHandle.setAccessible(true);
			Object nativeHandle = getNativeHandle.invoke(platformWindow);
			return (Long) nativeHandle;
		}
		catch(Throwable e)
		{
			System.err.println("Error getting Window Pointer");
			return null;
		}
	}

	// https://stackoverflow.com/questions/2773364/make-jface-window-blink-in-taskbar-or-get-users-attention
	public static void flashWindow(final Stage stage, boolean flashTray, boolean flashWindow)
	{
		try
		{
			if(stage.isFocused())
			{
				flashTray = false;
				flashWindow = false;
			}

			User32 lib = (User32) getLibrary("user32", User32.class);
			User32.FLASHWINFO flash = new User32.FLASHWINFO();
			flash.hWnd = new WinNT.HANDLE(new WinDef.UINT_PTR(getWindowPointer(stage))
					.toPointer());
			flash.uCount = 2;
			flash.dwTimeout = 1000;
			if(flashTray || flashWindow)
			{
				flash.dwFlags = (flashTray ? User32.FLASHW_TRAY : WinUser.FLASHW_STOP) | (flashWindow ? User32.FLASHW_CAPTION : WinUser.FLASHW_STOP);
			}
			else
			{
				flash.dwFlags = User32.FLASHW_STOP;
			}
			flash.cbSize = flash.size();
			lib.FlashWindowEx(flash);
		}
		catch(UnsatisfiedLinkError e)
		{
		}
	}

	protected static StdCallLibrary getLibrary(String libraryName, Class<?> interfaceClass) throws UnsatisfiedLinkError
	{
		try
		{
			return (StdCallLibrary) Native.loadLibrary(libraryName, interfaceClass);
		}
		catch(UnsatisfiedLinkError e)
		{
			LoggerBridge.error("Could not load " + libraryName + " library.");
			throw e;
		}
	}
}
