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
	@NativeFeatureNotSupported
	public void requestUserAttentionByStage(Stage stage)
	{
	}

	@Override
	@NativeFeatureNotSupported
	public void cancelUserAttention()
	{

	}

	@Override
	public void cancelUserAttentionByStage(Stage stage)
	{
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
			throw new UncheckedIOException(e);
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

}
