package de.thecodelabs.utils.application.system.impl;

import de.thecodelabs.utils.application.NativeLoader;
import de.thecodelabs.utils.application.system.NativeApplication;
import de.thecodelabs.utils.application.system.NativeFeatureNotSupported;
import de.thecodelabs.utils.io.IOUtils;
import de.thecodelabs.utils.util.ImageUtils;
import de.thecodelabs.utils.util.OS;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.nio.file.Path;

public class MacNativeApplication extends NativeApplication
{
	private long userAttentionRequestId = -1;

	private static boolean loaded = false;

	private static void loadNativeLibrary()
	{
		if(!loaded && OS.isMacOS())
		{
			NativeLoader.load("libUtilsNative.dylib", "libraries", MacNativeApplication.class);
			loaded = !loaded;
		}
	}

	public MacNativeApplication()
	{
		loadNativeLibrary();
	}

	@Override
	public void preventSystemSleep(boolean on)
	{
		preventSystemSleep_N(on);
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
	public void requestUserAttention(RequestUserAttentionType requestUserAttentionType)
	{
		if (userAttentionRequestId != -1) {
			cancelUserAttention();
		}

		if(requestUserAttentionType == RequestUserAttentionType.INFORMATIONAL_REQUEST)
		{
			userAttentionRequestId = requestUserInformationAttention_N();
		}
		else if(requestUserAttentionType == RequestUserAttentionType.CRITICAL_REQUEST)
		{
			userAttentionRequestId = requestUserCriticalAttention_N();
		}
	}

	@Override
	@NativeFeatureNotSupported
	public void requestUserAttentionByStage(Stage stage)
	{
	}

	@Override
	public void cancelUserAttention()
	{
		cancelUserAttention_N(userAttentionRequestId);
		userAttentionRequestId = -1;
	}

	@Override
	@NativeFeatureNotSupported
	public void cancelUserAttentionByStage(Stage stage)
	{
	}

	@Override
	public void setDockIcon(Image image)
	{
		setDockIcon_N(ImageUtils.imageToByteArray(image));
	}

	@Override
	public void setDockIconBadge(int i)
	{
		setDockIconBadge_N(i);
	}

	@Override
	public void setDockIconHidden(boolean hidden)
	{
		setDockIconHidden_N(hidden);
	}

	@Override
	public void setAppearance(boolean darkAqua)
	{
		setAppearance_N(darkAqua);
	}

	@Override
	public void showFileInFileViewer(Path path)
	{
		showFileInFileViewer_N(path.toString());
	}

	@Override
	public Image getImageForFile(Path file)
	{
		return new Image(IOUtils.byteArrayToInputStream(getImageForFile_N(file.toString())));
	}

	/*
	 Native methods
	 */

	private static native void preventSystemSleep_N(boolean on);

	private static native long requestUserInformationAttention_N();

	private static native long requestUserCriticalAttention_N();

	private static native void cancelUserAttention_N(long id);

	private static native void setDockIcon_N(byte[] image);

	private static native void setDockIconBadge_N(int i);

	private static native void setDockIconHidden_N(boolean hidden);

	private static native void setAppearance_N(boolean darkAqua);

	private static native void showFileInFileViewer_N(String file);

	private static native byte[] getImageForFile_N(String path);
}
