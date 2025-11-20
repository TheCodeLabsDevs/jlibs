package de.thecodelabs.utils.application.system.impl;

import de.thecodelabs.utils.application.NativeLoader;
import de.thecodelabs.utils.application.system.NativeApplication;
import de.thecodelabs.utils.application.system.NativeFeatureNotSupported;
import de.thecodelabs.utils.ui.ImageUtils;
import de.thecodelabs.utils.util.OS;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandle;
import java.nio.file.Path;

import static de.thecodelabs.utils.application.system.impl.MacObjcRuntime.*;

@SuppressWarnings({"java:S112", "java:S115", "java:S117", "java:S125"})
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
		MacSleepPrevent.setPreventSleep(on);
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
		if(userAttentionRequestId != -1)
		{
			cancelUserAttention();
		}

		if(requestUserAttentionType == RequestUserAttentionType.INFORMATIONAL_REQUEST)
		{
			userAttentionRequestId = requestUserAttention(10); // 10 -> NSInformationalRequest
		}
		else if(requestUserAttentionType == RequestUserAttentionType.CRITICAL_REQUEST)
		{
			userAttentionRequestId = requestUserAttention(0); // 10 -> NSCriticalRequest
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
		cancelUserAttention(userAttentionRequestId);
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
		final byte[] imageData = ImageUtils.imageToByteArray(image);
		try(Arena arena = Arena.ofConfined())
		{
			// 1. NSData *d = [NSData dataWithBytes:data length:lenght];
			final MemorySegment nsDataClass = (MemorySegment) objc_getClass.invoke(arena.allocateFrom("NSData"));
			final MemorySegment selDataWithBytes = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("dataWithBytes:length:"));

			final MemorySegment nativeData = arena.allocate(imageData.length);
			nativeData.copyFrom(MemorySegment.ofArray(imageData));

			final MethodHandle objc_msgSend2Data = LINKER.downcallHandle(
					OBJC.find("objc_msgSend").orElseThrow(),
					FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.JAVA_INT)
			);
			final MemorySegment nsData = (MemorySegment) objc_msgSend2Data.invoke(nsDataClass, selDataWithBytes, nativeData, imageData.length);

			// 2. NSImage *image = [[NSImage alloc] initWithData:d];
			final MemorySegment nsImageClass = (MemorySegment) objc_getClass.invoke(arena.allocateFrom("NSImage"));
			final MemorySegment selAlloc = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("alloc"));
			final MemorySegment imageAlloc = (MemorySegment) objc_msgSend0.invoke(nsImageClass, selAlloc);

			final MemorySegment selInitWithData = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("initWithData:"));
			final MemorySegment nsImage = (MemorySegment) objc_msgSend1.invoke(imageAlloc, selInitWithData, nsData);

			// 3. [NSApp setApplicationIconImage:image]
			final MemorySegment selSetIcon = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("setApplicationIconImage:"));
			objc_msgSend1.invoke(sharedApp(arena), selSetIcon, nsImage);
		}
		catch(Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public void setDockIconBadge(int i)
	{
		try(Arena arena = Arena.ofConfined())
		{
			// Selector: requestUserAttention:
			MemorySegment selRequestAttention = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("setBadgeLabel:"));

			final MethodHandle objc_msgSend1 = LINKER.downcallHandle(
					OBJC.find("objc_msgSend").orElseThrow(),
					FunctionDescriptor.of(
							ValueLayout.ADDRESS,  // Return type (id)
							ValueLayout.ADDRESS,  // self
							ValueLayout.ADDRESS,  // SEL
							ValueLayout.ADDRESS   // arg1
					)
			);
			objc_msgSend1.invoke(dockTile(arena), selRequestAttention, nsString(arena, i == 0 ? "" : String.valueOf(i)));
		}
		catch(Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public void setDockIconHidden(boolean hidden)
	{
		try(Arena arena = Arena.ofConfined())
		{
			// Selector: setActivationPolicy:
			MemorySegment selRequestAttention = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("setActivationPolicy:"));

			final MethodHandle objc_msgSend1 = LINKER.downcallHandle(
					OBJC.find("objc_msgSend").orElseThrow(),
					FunctionDescriptor.of(
							ValueLayout.ADDRESS,  // Return type (id)
							ValueLayout.ADDRESS,  // self
							ValueLayout.ADDRESS,  // SEL
							ValueLayout.JAVA_INT   // arg1
					)
			);
			// 0 --> NSApplicationActivationPolicyRegular
			// 1 --> NSApplicationActivationPolicyAccessory
			objc_msgSend1.invoke(sharedApp(arena), selRequestAttention, hidden ? 1 : 0);
		}
		catch(Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}

	@Override
	public void showFileInFileViewer(Path path)
	{
		try(Arena arena = Arena.ofConfined())
		{
			// NSString*
			final MemorySegment nsFile = nsString(arena, path.toAbsolutePath().toString());

			// [NSWorkspace sharedWorkspace]
			final MemorySegment nsWorkspaceClass = (MemorySegment) objc_getClass.invoke(arena.allocateFrom("NSWorkspace"));
			final MemorySegment selSharedWorkspace = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("sharedWorkspace"));
			final MemorySegment workspace = (MemorySegment) objc_msgSend1.invoke(nsWorkspaceClass, selSharedWorkspace, MemorySegment.NULL);

			// Selector: selectFile:inFileViewerRootedAtPath:
			final MemorySegment selSelectFile = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("selectFile:inFileViewerRootedAtPath:"));

			// Aufruf: [workspace selectFile:nsFile inFileViewerRootedAtPath:nsFile]
			objc_msgSend2.invoke(workspace, selSelectFile, nsFile, nsFile);
		}
		catch(Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}

	/*
	 Native methods
	 */

	private static long requestUserAttention(int level)
	{
		try(Arena arena = Arena.ofConfined())
		{
			// Selector: requestUserAttention:
			MemorySegment selRequestAttention = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("requestUserAttention:"));

			final MethodHandle objc_msgSend1 = LINKER.downcallHandle(
					OBJC.find("objc_msgSend").orElseThrow(),
					FunctionDescriptor.of(
							ValueLayout.JAVA_LONG,  // Return type (long)
							ValueLayout.ADDRESS,  // self
							ValueLayout.ADDRESS,  // SEL
							ValueLayout.JAVA_INT   // arg1
					)
			);
			return (long) objc_msgSend1.invoke(sharedApp(arena), selRequestAttention, level);
		}
		catch(Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}

	private static void cancelUserAttention(long id)
	{
		try(Arena arena = Arena.ofConfined())
		{
			// Selector: cancelUserAttentionRequest:
			MemorySegment selRequestAttention = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("cancelUserAttentionRequest:"));

			MethodHandle objc_msgSend1 = LINKER.downcallHandle(
					OBJC.find("objc_msgSend").orElseThrow(),
					FunctionDescriptor.of(
							ValueLayout.ADDRESS,  // Return type (id)
							ValueLayout.ADDRESS,  // self
							ValueLayout.ADDRESS,  // SEL
							ValueLayout.JAVA_LONG   // arg1
					)
			);

			objc_msgSend1.invoke(sharedApp(arena), selRequestAttention, id);
		}
		catch(Throwable throwable)
		{
			throw new RuntimeException(throwable);
		}
	}
}
