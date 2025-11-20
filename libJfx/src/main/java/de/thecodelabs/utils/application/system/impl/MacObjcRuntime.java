package de.thecodelabs.utils.application.system.impl;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;

class MacObjcRuntime
{
	static final Linker LINKER = Linker.nativeLinker();
	static final Arena GLOBAL = Arena.global();

	// ObjC Runtime
	static final SymbolLookup OBJC = SymbolLookup.libraryLookup("/usr/lib/libobjc.A.dylib", GLOBAL);

	// Grundlegende ObjC Funktionen
	static final MethodHandle objc_getClass = LINKER.downcallHandle(
			OBJC.find("objc_getClass").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS)
	);

	static final MethodHandle sel_registerName = LINKER.downcallHandle(
			OBJC.find("sel_registerName").orElseThrow(),
			FunctionDescriptor.of(ValueLayout.ADDRESS, ValueLayout.ADDRESS)
	);

	// objc_msgSend für Methoden mit 0 Parameter (nach self + sel)
	static final MethodHandle objc_msgSend0 = LINKER.downcallHandle(
			OBJC.find("objc_msgSend").orElseThrow(),
			FunctionDescriptor.of(
					ValueLayout.ADDRESS,
					ValueLayout.ADDRESS,  // self
					ValueLayout.ADDRESS   // SEL
			)
	);

	// objc_msgSend für Methoden mit 1 Parameter (nach self + sel)
	static final MethodHandle objc_msgSend1 = LINKER.downcallHandle(
			OBJC.find("objc_msgSend").orElseThrow(),
			FunctionDescriptor.of(
					ValueLayout.ADDRESS,  // Return type (id)
					ValueLayout.ADDRESS,  // self
					ValueLayout.ADDRESS,  // SEL
					ValueLayout.ADDRESS   // arg1
			)
	);

	// objc_msgSend für Methoden mit 2 Parametern (nach self + sel)
	static final MethodHandle objc_msgSend2 = LINKER.downcallHandle(
			OBJC.find("objc_msgSend").orElseThrow(),
			FunctionDescriptor.of(
					ValueLayout.ADDRESS,  // Return type (id)
					ValueLayout.ADDRESS,  // self
					ValueLayout.ADDRESS,  // SEL
					ValueLayout.ADDRESS,  // arg1
					ValueLayout.ADDRESS   // arg2
			)
	);

	// NSString helper
	static MemorySegment nsString(Arena arena, String s) throws Throwable
	{
		final MemorySegment nsStringClass = (MemorySegment) objc_getClass.invoke(arena.allocateFrom("NSString"));
		final MemorySegment selStringWithUTF8 = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("stringWithUTF8String:"));
		final MemorySegment cStr = arena.allocateFrom(s);
		return (MemorySegment) objc_msgSend1.invoke(nsStringClass, selStringWithUTF8, cStr);
	}

	static MemorySegment sharedApp(Arena arena) throws Throwable
	{
		// NSApplication Klasse
		final MemorySegment nsAppClass = (MemorySegment) objc_getClass.invoke(arena.allocateFrom("NSApplication"));
		final MemorySegment selSharedApp = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("sharedApplication"));
		return (MemorySegment) objc_msgSend0.invoke(nsAppClass, selSharedApp);
	}

	static MemorySegment dockTile(Arena arena) throws Throwable
	{
		// NSApplication Klasse
		final MemorySegment selDockTile = (MemorySegment) sel_registerName.invoke(arena.allocateFrom("dockTile"));
		return (MemorySegment) objc_msgSend0.invoke(sharedApp(arena), selDockTile);
	}

	private MacObjcRuntime()
	{
	}
}
