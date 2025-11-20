package de.thecodelabs.utils.application.system.impl;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

@SuppressWarnings({"java:S112", "java:S115"})
class MacSleepPrevent
{
	private static final Linker LINKER = Linker.nativeLinker();
	private static final Arena GLOBAL = Arena.global();

	private static final SymbolLookup IO_KIT = SymbolLookup.libraryLookup(
			"/System/Library/Frameworks/IOKit.framework/IOKit", GLOBAL);
	private static final SymbolLookup CORE_FOUNDATION = SymbolLookup.libraryLookup(
			"/System/Library/Frameworks/CoreFoundation.framework/CoreFoundation", GLOBAL);

	// Konstanten
	private static final int kIOPMAssertionLevelOn = 255;
	private static final int kCFStringEncodingUTF8 = 0x08000100;

	// MethodHandles
	private static final MethodHandle CFStringCreateWithCString;
	private static final MethodHandle IOPMAssertionCreateWithName;
	private static final MethodHandle IOPMAssertionRelease;

	private static volatile int currentAssertionId = -1;

	static
	{
		try
		{
			// CFStringCreateWithCString(CFAllocatorRef, const char*, CFStringEncoding)
			CFStringCreateWithCString = LINKER.downcallHandle(
					CORE_FOUNDATION.find("CFStringCreateWithCString").orElseThrow(),
					FunctionDescriptor.of(
							ValueLayout.ADDRESS,   // Return CFStringRef
							ValueLayout.ADDRESS,   // CFAllocatorRef
							ValueLayout.ADDRESS,   // const char* cStr
							ValueLayout.JAVA_INT   // CFStringEncoding
					)
			);

			// IOPMAssertionCreateWithName(CFStringRef type, int level, CFStringRef name, uint32_t* id)
			IOPMAssertionCreateWithName = LINKER.downcallHandle(
					IO_KIT.find("IOPMAssertionCreateWithName").orElseThrow(),
					FunctionDescriptor.of(
							ValueLayout.JAVA_INT,  // IOReturn
							ValueLayout.ADDRESS,   // CFStringRef assertion type
							ValueLayout.JAVA_INT,  // assertion level
							ValueLayout.ADDRESS,   // CFStringRef assertion name
							ValueLayout.ADDRESS    // uint32_t* assertion ID
					)
			);

			// IOPMAssertionRelease(uint32_t id)
			IOPMAssertionRelease = LINKER.downcallHandle(
					IO_KIT.find("IOPMAssertionRelease").orElseThrow(),
					FunctionDescriptor.of(
							ValueLayout.JAVA_INT,  // IOReturn
							ValueLayout.JAVA_INT   // uint32_t id
					)
			);

		}
		catch(Exception t)
		{
			throw new ExceptionInInitializerError(t);
		}
	}

	private MacSleepPrevent()
	{
	}

	private static MemorySegment createCFString(Arena arena, String s)
	{
		try
		{
			MemorySegment cStr = arena.allocateFrom(s);
			return (MemorySegment) CFStringCreateWithCString.invoke(
					MemorySegment.NULL, // default allocator
					cStr,
					kCFStringEncodingUTF8
			);
		}
		catch(Throwable t)
		{
			throw new RuntimeException("Failed to create CFString", t);
		}
	}

	/**
	 * Aktiviert oder deaktiviert die macOS-Assertion
	 */
	public static void setPreventSleep(boolean on)
	{
		if(on)
		{
			try(Arena arena = Arena.ofConfined())
			{
				MemorySegment assertionIdPtr = arena.allocate(ValueLayout.JAVA_INT);

				MemorySegment assertionType = createCFString(arena, "PreventUserIdleDisplaySleep");
				MemorySegment reason = createCFString(arena, "Program is still active");

				int ret = (int) IOPMAssertionCreateWithName.invoke(
						assertionType,
						kIOPMAssertionLevelOn,
						reason,
						assertionIdPtr
				);

				if(ret != 0)
				{
					throw new RuntimeException("IOPMAssertionCreateWithName error: " + ret);
				}

				currentAssertionId = assertionIdPtr.get(ValueLayout.JAVA_INT, 0);

			}
			catch(Throwable t)
			{
				throw new RuntimeException("Failed to create IOPM assertion", t);
			}
		}
		else
		{
			int id = currentAssertionId;
			if(id != -1)
			{
				try
				{
					int ret = (int) IOPMAssertionRelease.invoke(id);
					if(ret != 0)
					{
						throw new RuntimeException("IOPMAssertionRelease returned error: " + ret);
					}
				}
				catch(Throwable t)
				{
					throw new RuntimeException("Failed to release IOPM assertion", t);
				}
				finally
				{
					currentAssertionId = -1;
				}
			}
		}
	}
}
