package de.thecodelabs.midi.device.coremidi;

import de.thecodelabs.midi.device.MidiDevice;
import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.MidiInputPublisher;
import de.thecodelabs.midi.midi.MidiMessage;

import javax.sound.midi.MidiUnavailableException;
import java.lang.foreign.Arena;
import java.lang.foreign.FunctionDescriptor;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

class CoreMidiDevice extends MidiDevice
{
	private final int sourceEndpointRef; // MIDIEndpointRef (UInt32), 0 = none
	private final int destEndpointRef;   // MIDIEndpointRef (UInt32), 0 = none

	private int inputPortRef;
	private int outputPortRef;
	private Arena callbackArena; // owns the upcall stub; shared because callback fires on CoreMIDI thread

	CoreMidiDevice(final MidiInputPublisher publisher, final MidiDeviceInfo deviceInfo, final int sourceEndpointRef, final int destEndpointRef)
	{
		super(deviceInfo, publisher);
		this.sourceEndpointRef = sourceEndpointRef;
		this.destEndpointRef = destEndpointRef;
	}

	void open(final Midi.Mode... modes) throws MidiUnavailableException
	{
		for(final Midi.Mode mode : modes)
		{
			switch(mode)
			{
				case INPUT ->
				{
					if(sourceEndpointRef != 0) openInput();
				}
				case OUTPUT ->
				{
					if(destEndpointRef != 0) openOutput();
				}
			}
		}
	}

	private void openInput() throws MidiUnavailableException
	{
		callbackArena = Arena.ofShared();
		final MemorySegment upcallStub;
		try
		{
			final var mt = MethodType.methodType(void.class, MemorySegment.class, MemorySegment.class, MemorySegment.class);
			final var target = MethodHandles.lookup()
					.findVirtual(CoreMidiDevice.class, "onMidiRead", mt)
					.bindTo(this);
			upcallStub = CoreMidiLibrary.LINKER.upcallStub(
					target,
					FunctionDescriptor.ofVoid(ValueLayout.ADDRESS, ValueLayout.ADDRESS, ValueLayout.ADDRESS),
					callbackArena);
		}
		catch(final NoSuchMethodException | IllegalAccessException e)
		{
			callbackArena.close();
			callbackArena = null;
			throw new MidiUnavailableException("Failed to create MIDI read callback: " + e.getMessage());
		}

		final String portName = "libMidi-in-" + getMidiDeviceInfo().name();
		inputPortRef = CoreMidiLibrary.inputPortCreate(CoreMidiLibrary.CLIENT_REF, portName, upcallStub);
		if(inputPortRef == 0)
		{
			callbackArena.close();
			callbackArena = null;
			throw new MidiUnavailableException("MIDIInputPortCreate failed for: " + getMidiDeviceInfo().name());
		}

		final int connectStatus = CoreMidiLibrary.portConnectSource(inputPortRef, sourceEndpointRef);
		if(connectStatus != 0)
		{
			CoreMidiLibrary.portDispose(inputPortRef);
			inputPortRef = 0;
			callbackArena.close();
			callbackArena = null;
			throw new MidiUnavailableException("MIDIPortConnectSource failed with OSStatus " + connectStatus);
		}
	}

	private void openOutput() throws MidiUnavailableException
	{
		final String portName = "libMidi-out-" + getMidiDeviceInfo().name();
		outputPortRef = CoreMidiLibrary.outputPortCreate(CoreMidiLibrary.CLIENT_REF, portName);
		if(outputPortRef == 0)
		{
			throw new MidiUnavailableException("MIDIOutputPortCreate failed for: " + getMidiDeviceInfo().name());
		}
	}

	/**
	 * Receives MIDI packets from CoreMIDI on a CoreMIDI background thread.
	 * The {@code pktlist} argument points to a MIDIPacketList struct:
	 * <pre>
	 *   offset 0: numPackets (UInt32)
	 *   offset 4: MIDIPacket[] where each packet is:
	 *     +0: timeStamp (UInt64, 8 bytes)
	 *     +8: length    (UInt16, 2 bytes)
	 *     +10: data[length]
	 *     next packet at: ((10 + length) + 3) & ~3  (4-byte aligned)
	 * </pre>
	 */
	private void onMidiRead(final MemorySegment pktlist, final MemorySegment readProcRefCon, final MemorySegment srcConnRefCon)
	{
		final MemorySegment packets = pktlist.reinterpret(Long.MAX_VALUE);
		final int numPackets = packets.get(ValueLayout.JAVA_INT, 0);
		long offset = 4L; // first MIDIPacket

		for(int i = 0; i < numPackets; i++)
		{
			final int length = Short.toUnsignedInt(packets.get(ValueLayout.JAVA_SHORT, offset + 8));
			if(length > 0)
			{
				final byte[] data = new byte[length];
				MemorySegment.copy(packets, ValueLayout.JAVA_BYTE, offset + 10, MemorySegment.ofArray(data), ValueLayout.JAVA_BYTE, 0, length);
				publisher.publish(new MidiMessage(data));
			}
			// Advance to next MIDIPacket (MIDIPacketNext macro logic: 4-byte aligned)
			offset = (offset + 10 + length + 3L) & ~3L;
		}
	}

	@Override
	public void sendMidiMessage(final MidiMessage midiEvent)
	{
		if(!isModeSupported(Midi.Mode.OUTPUT)) return;
		if(midiEvent.getMessageType() == null) return;

		final byte[] payload = midiEvent.getPayload();
		if(payload.length < 2) return;

		final byte statusByte = (byte) (midiEvent.getMessageType().getMidiValue() + midiEvent.getChannel());

		// MIDIPacketList layout:
		//   offset  0: numPackets = 1  (UInt32)
		//   offset  4: timeStamp = 0   (UInt64, "now")
		//   offset 12: length = 3      (UInt16)
		//   offset 14: status, data1, data2 (3 × Byte)
		try(final Arena arena = Arena.ofConfined())
		{
			final MemorySegment pktlist = arena.allocate(20, 4);
			pktlist.set(ValueLayout.JAVA_INT, 0, 1);
			pktlist.set(ValueLayout.JAVA_LONG, 4, 0L);
			pktlist.set(ValueLayout.JAVA_SHORT, 12, (short) 3);
			pktlist.set(ValueLayout.JAVA_BYTE, 14, statusByte);
			pktlist.set(ValueLayout.JAVA_BYTE, 15, payload[0]);
			pktlist.set(ValueLayout.JAVA_BYTE, 16, payload[1]);

			final int status = CoreMidiLibrary.midiSend(outputPortRef, destEndpointRef, pktlist);
			if(status != 0) throw new RuntimeException("MIDISend failed with OSStatus " + status);
		}
	}

	@Override
	public void closeDevice()
	{
		if(inputPortRef != 0)
		{
			CoreMidiLibrary.portDisconnectSource(inputPortRef, sourceEndpointRef);
			CoreMidiLibrary.portDispose(inputPortRef);
			inputPortRef = 0;
		}
		if(callbackArena != null)
		{
			callbackArena.close();
			callbackArena = null;
		}
		if(outputPortRef != 0)
		{
			CoreMidiLibrary.portDispose(outputPortRef);
			outputPortRef = 0;
		}
	}

	@Override
	public boolean isOpen()
	{
		return inputPortRef != 0 || outputPortRef != 0;
	}

	@Override
	public boolean isModeSupported(final Midi.Mode mode)
	{
		return switch(mode)
		{
			case INPUT -> inputPortRef != 0;
			case OUTPUT -> outputPortRef != 0;
		};
	}
}
