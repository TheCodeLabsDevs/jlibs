package de.thecodelabs.midi.midi.device.coremidi;

import java.lang.foreign.*;
import java.lang.invoke.MethodHandle;
import java.nio.charset.StandardCharsets;

/**
 * FFM bindings to CoreMIDI and CoreFoundation frameworks.
 * Holds a singleton MIDIClientRef shared across all CoreMidiDevice instances.
 */
final class CoreMidiLibrary
{
	private static final int CF_STRING_ENCODING_UTF8 = 0x08000100;

	private static final SymbolLookup CORE_MIDI;
	private static final SymbolLookup CORE_FOUNDATION;

	static final Linker LINKER = Linker.nativeLinker();

	// CoreMIDI function handles
	private static final MethodHandle h_MIDIObjectGetIntegerProperty;
	private static final MethodHandle h_MIDIGetNumberOfDevices;
	private static final MethodHandle h_MIDIGetDevice;
	private static final MethodHandle h_MIDIDeviceGetNumberOfEntities;
	private static final MethodHandle h_MIDIDeviceGetEntity;
	private static final MethodHandle h_MIDIEntityGetNumberOfSources;
	private static final MethodHandle h_MIDIEntityGetSource;
	private static final MethodHandle h_MIDIEntityGetNumberOfDestinations;
	private static final MethodHandle h_MIDIEntityGetDestination;
	private static final MethodHandle h_MIDIObjectGetStringProperty;
	private static final MethodHandle h_MIDIClientCreate;
	private static final MethodHandle h_MIDIInputPortCreate;
	private static final MethodHandle h_MIDIOutputPortCreate;
	private static final MethodHandle h_MIDIPortConnectSource;
	private static final MethodHandle h_MIDIPortDisconnectSource;
	private static final MethodHandle h_MIDIPortDispose;
	private static final MethodHandle h_MIDISend;
	private static final MethodHandle h_MIDIClientDispose;

	// CoreFoundation function handles
	private static final MethodHandle h_CFStringCreateWithCString;
	private static final MethodHandle h_CFStringGetCString;
	private static final MethodHandle h_CFRelease;

	static final MemorySegment K_MIDI_PROPERTY_NAME;
	static final MemorySegment K_MIDI_PROPERTY_MANUFACTURER;
	static final MemorySegment K_MIDI_PROPERTY_OFFLINE;

	static final int CLIENT_REF;

	static
	{
		CORE_MIDI = SymbolLookup.libraryLookup(
				"/System/Library/Frameworks/CoreMIDI.framework/CoreMIDI", Arena.global());
		CORE_FOUNDATION = SymbolLookup.libraryLookup(
				"/System/Library/Frameworks/CoreFoundation.framework/CoreFoundation", Arena.global());

		h_MIDIObjectGetIntegerProperty = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIObjectGetIntegerProperty"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,   // MIDIObjectRef obj
						ValueLayout.ADDRESS,    // CFStringRef propertyID
						ValueLayout.ADDRESS));  // SInt32 *outValue

		h_MIDIGetNumberOfDevices = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIGetNumberOfDevices"),
				FunctionDescriptor.of(ValueLayout.JAVA_LONG));

		h_MIDIGetDevice = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIGetDevice"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_LONG));  // ItemCount deviceIndex0

		h_MIDIDeviceGetNumberOfEntities = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIDeviceGetNumberOfEntities"),
				FunctionDescriptor.of(ValueLayout.JAVA_LONG,
						ValueLayout.JAVA_INT));   // MIDIDeviceRef device

		h_MIDIDeviceGetEntity = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIDeviceGetEntity"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,     // MIDIDeviceRef device
						ValueLayout.JAVA_LONG));  // ItemCount entityIndex0

		h_MIDIEntityGetNumberOfSources = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIEntityGetNumberOfSources"),
				FunctionDescriptor.of(ValueLayout.JAVA_LONG,
						ValueLayout.JAVA_INT));   // MIDIEntityRef entity

		h_MIDIEntityGetSource = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIEntityGetSource"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,     // MIDIEntityRef entity
						ValueLayout.JAVA_LONG));  // ItemCount sourceIndex0

		h_MIDIEntityGetNumberOfDestinations = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIEntityGetNumberOfDestinations"),
				FunctionDescriptor.of(ValueLayout.JAVA_LONG,
						ValueLayout.JAVA_INT));   // MIDIEntityRef entity

		h_MIDIEntityGetDestination = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIEntityGetDestination"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT,     // MIDIEntityRef entity
						ValueLayout.JAVA_LONG));  // ItemCount destIndex0

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

		h_MIDIClientDispose = LINKER.downcallHandle(
				sym(CORE_MIDI, "MIDIClientDispose"),
				FunctionDescriptor.of(ValueLayout.JAVA_INT,
						ValueLayout.JAVA_INT)); // MIDIClientRef client

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

		// kMIDIPropertyName etc. are lazily initialized by CoreMIDI — null until MIDIClientCreate runs.
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

			K_MIDI_PROPERTY_NAME        = readPointerSymbol(CORE_MIDI, "kMIDIPropertyName");
			K_MIDI_PROPERTY_MANUFACTURER = readPointerSymbol(CORE_MIDI, "kMIDIPropertyManufacturer");
			K_MIDI_PROPERTY_OFFLINE      = readPointerSymbol(CORE_MIDI, "kMIDIPropertyOffline");
		}
		catch(final Throwable t)
		{
			throw new ExceptionInInitializerError(t);
		}
	}

	// --- Device / Entity hierarchy ---

	static long getNumberOfDevices()
	{
		try
		{
			return (long) h_MIDIGetNumberOfDevices.invokeExact();
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int getDevice(final long index)
	{
		try
		{
			return (int) h_MIDIGetDevice.invokeExact(index);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static long getNumberOfEntities(final int deviceRef)
	{
		try
		{
			return (long) h_MIDIDeviceGetNumberOfEntities.invokeExact(deviceRef);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int getEntity(final int deviceRef, final long entityIndex)
	{
		try
		{
			return (int) h_MIDIDeviceGetEntity.invokeExact(deviceRef, entityIndex);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static long getNumberOfEntitySources(final int entityRef)
	{
		try
		{
			return (long) h_MIDIEntityGetNumberOfSources.invokeExact(entityRef);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int getEntitySource(final int entityRef, final long index)
	{
		try
		{
			return (int) h_MIDIEntityGetSource.invokeExact(entityRef, index);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static long getNumberOfEntityDestinations(final int entityRef)
	{
		try
		{
			return (long) h_MIDIEntityGetNumberOfDestinations.invokeExact(entityRef);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	static int getEntityDestination(final int entityRef, final long index)
	{
		try
		{
			return (int) h_MIDIEntityGetDestination.invokeExact(entityRef, index);
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

	static int getIntegerProperty(final int objectRef, final MemorySegment propertyKey)
	{
		try(final Arena arena = Arena.ofConfined())
		{
			final MemorySegment outValue = arena.allocate(ValueLayout.JAVA_INT);
			final int status = (int) h_MIDIObjectGetIntegerProperty.invokeExact(objectRef, propertyKey, outValue);
			return status == 0 ? outValue.get(ValueLayout.JAVA_INT, 0) : 0;
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
			// Ignore non-zero status — the source endpoint may already be gone (device disconnect).
			h_MIDIPortDisconnectSource.invokeExact(portRef, sourceRef);
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
			final int status = (int) h_MIDIPortDispose.invokeExact(portRef);
			if(status != 0) throw new RuntimeException("MIDIPortDispose failed with OSStatus " + status);
		}
		catch(final Throwable t)
		{
			throw new RuntimeException(t);
		}
	}

	// --- Client lifecycle ---

	static void disposeClient()
	{
		try
		{
			final int status = (int) h_MIDIClientDispose.invokeExact(CLIENT_REF);
			if(status != 0) throw new RuntimeException("MIDIClientDispose failed with OSStatus " + status);
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
	private static MemorySegment createCFStringRaw(final String s, final Arena arena)
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

	private static void cfRelease(final MemorySegment cfRef)
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
