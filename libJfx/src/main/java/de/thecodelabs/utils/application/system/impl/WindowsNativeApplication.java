package de.thecodelabs.utils.application.system.impl;

import de.thecodelabs.utils.application.NativeLoader;
import de.thecodelabs.utils.application.system.NativeApplication;
import de.thecodelabs.utils.application.system.NativeFeatureNotSupported;
import de.thecodelabs.utils.io.IOUtils;
import de.thecodelabs.utils.util.OS;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.Linker;
import java.lang.foreign.SymbolLookup;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

public class WindowsNativeApplication extends NativeApplication
{
	// Flags aus WinBase.h
	private static final int ES_AWAYMODE_REQUIRED = 0x00000040;
	private static final int ES_CONTINUOUS = 0x80000000;
	private static final int ES_DISPLAY_REQUIRED = 0x00000002;
	private static final int ES_SYSTEM_REQUIRED = 0x00000001;

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
	@NativeFeatureNotSupported
	public void preventSystemSleep(boolean on) throws Throwable
	{
		Linker linker = Linker.nativeLinker();
		SymbolLookup kernel32 = SymbolLookup.libraryLookup("kernel32.dll", Arena.global());

		MethodHandle setThreadExecutionState = linker.downcallHandle(
				kernel32.find("SetThreadExecutionState").orElseThrow(),
				FunctionDescriptor.of(
						java.lang.foreign.ValueLayout.JAVA_INT,
						java.lang.foreign.ValueLayout.JAVA_INT
				)
		);

		final int flags;
		if(on)
		{
			flags = ES_CONTINUOUS | ES_DISPLAY_REQUIRED | ES_SYSTEM_REQUIRED;
		}
		else
		{
			flags = ES_CONTINUOUS;
		}
		int result = (int) setThreadExecutionState.invoke(flags);
		if(on && result == 0)
		{
			throw new RuntimeException("Failed to set SetThreadExecutionState");
		}
	}

	@Override
	@NativeFeatureNotSupported
	public void executeAsAdministrator(String command, String args)
	{
	}

	@Override
	@NativeFeatureNotSupported
	public boolean isTouchInputAvailable()
	{
		return false;
	}

	@Override
	@NativeFeatureNotSupported
	public void requestUserAttention(RequestUserAttentionType requestUserAttentionType)
	{

	}

	@Override
	public void requestUserAttentionByStage(Stage stage)
	{
		//flashWindow(stage, true, true);
	}

	@Override
	@NativeFeatureNotSupported
	public void cancelUserAttention()
	{

	}

	@Override
	public void cancelUserAttentionByStage(Stage stage)
	{
		//flashWindow(stage, false, false);

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

//	private static Long getWindowPointer(Stage stage)
//	{
//		try
//		{
//			TKStage tkStage = stage.impl_getPeer(); // TODO Work for javajx 8 > javajx 11 has different naming
//			Method getPlatformWindow = tkStage.getClass().getDeclaredMethod("getPlatformWindow");
//			getPlatformWindow.setAccessible(true);
//			Object platformWindow = getPlatformWindow.invoke(tkStage);
//			Method getNativeHandle = platformWindow.getClass().getMethod("getNativeHandle");
//			getNativeHandle.setAccessible(true);
//			Object nativeHandle = getNativeHandle.invoke(platformWindow);
//			return (Long) nativeHandle;
//		}
//		catch(Throwable e)
//		{
//			System.err.println("Error getting Window Pointer");
//			return null;
//		}
//	}
//
//	// https://stackoverflow.com/questions/2773364/make-jface-window-blink-in-taskbar-or-get-users-attention
//	public static void flashWindow(final Stage stage, boolean flashTray, boolean flashWindow)
//	{
//		try
//		{
//			if (stage.isFocused()) {
//				flashTray = false;
//				flashWindow = false;
//			}
//
//			User32 lib = (User32) getLibrary("user32", User32.class);
//			User32.FLASHWINFO flash = new User32.FLASHWINFO();
//			flash.hWnd = new WinNT.HANDLE(new WinDef.UINT_PTR(getWindowPointer(stage))
//					.toPointer());
//			flash.uCount = 2;
//			flash.dwTimeout = 1000;
//			if(flashTray || flashWindow)
//			{
//				flash.dwFlags = (flashTray ? User32.FLASHW_TRAY : WinUser.FLASHW_STOP) | (flashWindow ? User32.FLASHW_CAPTION : WinUser.FLASHW_STOP);
//			}
//			else
//			{
//				flash.dwFlags = User32.FLASHW_STOP;
//			}
//			flash.cbSize = flash.size();
//			lib.FlashWindowEx(flash);
//		}
//		catch(UnsatisfiedLinkError e)
//		{
//		}
//	}
//
//	protected static StdCallLibrary getLibrary(String libraryName,
//											   Class<?> interfaceClass) throws UnsatisfiedLinkError
//	{
//		try
//		{
//			StdCallLibrary lib = (StdCallLibrary) Native.loadLibrary(libraryName,
//					interfaceClass);
//			return lib;
//		}
//		catch(UnsatisfiedLinkError e)
//		{
//			LoggerBridge.error("Could not load " + libraryName + " library.");
//			throw e;
//		}
//	}
}
