package de.thecodelabs.midi.device.coremidi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

/**
 * FFM bindings to CoreMIDI and CoreFoundation frameworks.
 * Holds a singleton MIDIClientRef shared across all CoreMidiDevice instances.
 */
final class CoreMidiLibrary
{
	static final int CF_STRING_ENCODING_UTF8 = 0x08000100;

	private static final SymbolLookup CORE_MIDI;
	private static final SymbolLookup CORE_FOUNDATION;

	static final Linker LINKER = Linker.nativeLinker();

	// CoreMIDI function handles
	private static final MethodHandle h_MIDIGetNumberOfSources;
	private static final MethodHandle h_MIDIGetSource;
	private static final MethodHandle h_MIDIGetNumberOfDestinations;
	private static final MethodHandle h_MIDIGetDestination;
	private static final MethodHandle h_MIDIObjectGetStringProperty;
	private static final MethodHandle h_MIDIClientCreate;
	private static final MethodHandle h_MIDIInputPortCreate;
	private static final MethodHandle h_MIDIOutputPortCreate;
	private static final MethodHandle h_MIDIPortConnectSource;
	private static final MethodHandle h_MIDIPortDisconnectSource;
	private static final MethodHandle h_MIDIPortDispose;
	private static final MethodHandle h_MIDISend;

	// CoreFoundation function handles
	private static final MethodHandle h_CFStringCreateWithCString;
	private static final MethodHandle h_CFStringGetCString;
	private static final MethodHandle h_CFRelease;

	static final MemorySegment K_MIDI_PROPERTY_NAME;
	static final MemorySegment K_MIDI_PROPERTY_MANUFACTURER;

	static final int CLIENT_REF;

	static
	{
		CORE_MIDI = SymbolLookup.libraryLookup(
				"/System/Library/Frameworks/CoreMIDI.framework/CoreMIDI", Arena.global());
		CORE_FOUNDATION = SymbolLookup.libraryLookup(
				"/System/Library/Frameworks/CoreFoundation.framework/CoreFoundation", Arena.global());

		h_MIDIGetNumberOfSources = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIGetNumberOfSources"),
				FunctionDescriptor.of(ValueLayout.JAVA_LONG));

		h_MIDIGetSource = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIGetSource"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_LONG));  // ItemCount index

		h_MIDIGetNumberOfDestinations = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIGetNumberOfDestinations"),
				FunctionDescriptor.of(ValueLayout.JAVA_LONG));

		h_MIDIGetDestination = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIGetDestination"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_LONG));  // ItemCount index

		h_MIDIObjectGetStringProperty = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIObjectGetStringProperty"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,   // MIDIObjectRef obj
						ValueLayout.ADDRESS,    // CFStringRef propertyID
						ValueLayout.ADDRESS));  // CFStringRef *outStr

		h_MIDIClientCreate = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIClientCreate"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.ADDRESS,    // CFStringRef name
						ValueLayout.ADDRESS,    // MIDINotifyProc (nullable)
						ValueLayout.ADDRESS,    // void *notifyRefCon (nullable)
						ValueLayout.ADDRESS));  // MIDIClientRef *outClient

		h_MIDIInputPortCreate = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIInputPortCreate"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,   // MIDIClientRef client
						ValueLayout.ADDRESS,    // CFStringRef portName
						ValueLayout.ADDRESS,    // MIDIReadProc readProc
						ValueLayout.ADDRESS,    // void *refCon (nullable)
						ValueLayout.ADDRESS));  // MIDIPortRef *outPort

		h_MIDIOutputPortCreate = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIOutputPortCreate"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,   // MIDIClientRef client
						ValueLayout.ADDRESS,    // CFStringRef portName
						ValueLayout.ADDRESS));  // MIDIPortRef *outPort

		h_MIDIPortConnectSource = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIPortConnectSource"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,   // MIDIPortRef port
						ValueLayout.JAVA_INT,   // MIDIEndpointRef source
						ValueLayout.ADDRESS));  // void *connRefCon (nullable)

		h_MIDIPortDisconnectSource = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIPortDisconnectSource"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,   // MIDIPortRef port
						ValueLayout.JAVA_INT)); // MIDIEndpointRef source

		h_MIDIPortDispose = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIPortDispose"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT)); // MIDIPortRef port

		h_MIDISend = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDISend"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,   // MIDIPortRef port
						ValueLayout.JAVA_INT,   // MIDIEndpointRef dest
						ValueLayout.ADDRESS));  // const MIDIPacketList *pktlist

		h_CFStringCreateWithCString = LINKER.downcallHandle(
				sym(CORE_FOUNDATION, "CFStringCreateWithCString"),
				FunctionDescriptor.of(ValueLayout.ADDRESS,
						ValueLayout.ADDRESS,    // CFAllocatorRef alloc (nullable)
						ValueLayout.ADDRESS,    // const char *cStr
						ValueLayout.JAVA_INT)); // CFStringEncoding encoding

		h_CFStringGetCString = LINKER.downcallHandle(
				sym(CORE_FOUNDATION, "CFStringGetCString"),
				FunctionDescriptor.of(ValueLayout.JAVA_BYTE,
						ValueLayout.ADDRESS,    // CFStringRef theString
						ValueLayout.ADDRESS,    // char *buffer
						ValueLayout.JAVA_LONG,  // CFIndex bufferSize
						ValueLayout.JAVA_INT)); // CFStringEncoding encoding

		h_CFRelease = LINKER.downcallHandle(
				sym(CORE_FOUNDATION, "CFRelease"),
				FunctionDescriptor.ofVoid(ValueLayout.ADDRESS));

		// kMIDIPropertyName and kMIDIPropertyManufacturer are lazily initialized by CoreMIDI —
		// they are NULL (0x0) until MIDIClientCreate triggers CoreMIDI server initialization.
		// We must read them AFTER creating the MIDIClient.
		try(final Arena arena = Arena.ofConfined())
		{
			final MemorySegment cfName = createCFStringRaw("libMidi-CoreMidi", arena);
			final MemorySegment outClient = arena.allocate(ValueLayout.JAVA_INT);
			final int status = (int) h_MIDIClientCreate.invokeExact(
					cfName, MemorySegment.NULL, MemorySegment.NULL, outClient);
			cfRelease(cfName);
			if(status != 0)
			{
				throw new RuntimeException("MIDIClientCreate failed with OSStatus " + status);
			}
			CLIENT_REF = outClient.get(ValueLayout.JAVA_INT, 0);

			// Read property key constants now that CoreMIDI is initialized
			K_MIDI_PROPERTY_NAME = readPointerSymbol(CORE_MIDI, "kMIDIPropertyName");
			K_MIDI_PROPERTY_MANUFACTURER = readPointerSymbol(CORE_MIDI, "kMIDIPropertyManufacturer");
		}
		catch(final Throwable t)
		{
			throw new ExceptionInInitializerError(t);
		}
	}

	// --- Endpoint enumeration ---

	static long getNumberOfSources()
	{
		try
		{
			return (long) h_MIDIGetNumberOfSources.invokeExact();
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int getSource(final long index)
	{
		try
		{
			return (int) h_MIDIGetSource.invokeExact(index);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static long getNumberOfDestinations()
	{
		try
		{
			return (long) h_MIDIGetNumberOfDestinations.invokeExact();
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int getDestination(final long index)
	{
		try
		{
			return (int) h_MIDIGetDestination.invokeExact(index);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	// --- Property access ---

	static String getStringProperty(final int endpointRef, final MemorySegment propertyKey)
	{
		try(final Arena arena = Arena.ofConfined())
		{
			final MemorySegment outStr = arena.allocate(ValueLayout.ADDRESS);
			final int status = (int) h_MIDIObjectGetStringProperty.invokeExact(
					endpointRef, propertyKey, outStr);
			if(status != 0) return "";
			final MemorySegment cfStr = outStr.get(ValueLayout.ADDRESS, 0);
			if(cfStr.equals(MemorySegment.NULL)) return "";
			try
			{
				final MemorySegment buffer = arena.allocate(512);
				final byte ok = (byte) h_CFStringGetCString.invokeExact(
						cfStr, buffer, 512L, CF_STRING_ENCODING_UTF8);
				return ok != 0 ? buffer.getString(0, StandardCharsets.UTF_8) : "";
			}
			finally
			{
				cfRelease(cfStr);
			}
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	// --- Port management ---

	static int inputPortCreate(final int clientRef, final String portName, final MemorySegment readProc)
	{
		try(final Arena arena = Arena.ofConfined())
		{
			final MemorySegment cfName = createCFStringRaw(portName, arena);
			final MemorySegment outPort = arena.allocate(ValueLayout.JAVA_INT);
			final int status = (int) h_MIDIInputPortCreate.invokeExact(
					clientRef, cfName, readProc, MemorySegment.NULL, outPort);
			cfRelease(cfName);
			if(status != 0) return 0;
			return outPort.get(ValueLayout.JAVA_INT, 0);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int outputPortCreate(final int clientRef, final String portName)
	{
		try(final Arena arena = Arena.ofConfined())
		{
			final MemorySegment cfName = createCFStringRaw(portName, arena);
			final MemorySegment outPort = arena.allocate(ValueLayout.JAVA_INT);
			final int status = (int) h_MIDIOutputPortCreate.invokeExact(clientRef, cfName, outPort);
			cfRelease(cfName);
			if(status != 0) return 0;
			return outPort.get(ValueLayout.JAVA_INT, 0);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int portConnectSource(final int portRef, final int sourceRef)
	{
		try
		{
			return (int) h_MIDIPortConnectSource.invokeExact(portRef, sourceRef, MemorySegment.NULL);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static void portDisconnectSource(final int portRef, final int sourceRef)
	{
		try
		{
			h_MIDIPortDisconnectSource.invoke(portRef, sourceRef);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static void portDispose(final int portRef)
	{
		try
		{
			h_MIDIPortDispose.invoke(portRef);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	// --- MIDI send ---

	static int midiSend(final int portRef, final int destRef, final MemorySegment pktlist)
	{
		try
		{
			return (int) h_MIDISend.invokeExact(portRef, destRef, pktlist);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	// --- CFString helpers ---

	/**
	 * Creates a CFStringRef from a Java String. The returned segment is a CoreFoundation-managed
	 * object that must be released with {@link #cfRelease} when no longer needed.
	 * The {@code arena} is used only for the temporary UTF-8 buffer passed to CoreFoundation.
	 */
	static MemorySegment createCFStringRaw(final String s, final Arena arena)
	{
		try
		{
			final MemorySegment cStr = arena.allocateFrom(s, StandardCharsets.UTF_8);
			return (MemorySegment) h_CFStringCreateWithCString.invokeExact(
					MemorySegment.NULL, cStr, CF_STRING_ENCODING_UTF8);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static void cfRelease(final MemorySegment cfRef)
	{
		if(cfRef == null || cfRef.equals(MemorySegment.NULL)) return;
		try
		{
			h_CFRelease.invokeExact(cfRef);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	// --- Internal utilities ---

	private static MemorySegment sym(final SymbolLookup lookup, final String name)
	{
		return lookup.find(name).orElseThrow(() ->
				new RuntimeException("Symbol not found: " + name));
	}

	private static MemorySegment readPointerSymbol(final SymbolLookup lookup, final String name)
	{
		return lookup.find(name).orElseThrow()
				.reinterpret(ValueLayout.ADDRESS.byteSize())
				.get(ValueLayout.ADDRESS, 0);
	}

	private CoreMidiLibrary()
	{
	}
}
