package de.thecodelabs.midi.midi.device;

import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.message.MidiInputPublisher;
import de.thecodelabs.midi.midi.message.MidiMessage;

import java.util.concurrent.atomic.AtomicBoolean;

public abstract class MidiDevice
{
	private final MidiDeviceInfo midiDeviceInfo;
	protected final MidiInputPublisher publisher;

	private volatile Runnable disconnectCallback;
	private final AtomicBoolean disconnectFired = new AtomicBoolean(false);

	protected MidiDevice(MidiDeviceInfo midiDeviceInfo, MidiInputPublisher publisher)
	{
		this.midiDeviceInfo = midiDeviceInfo;
		this.publisher = publisher;
	}

	public void setDisconnectCallback(final Runnable callback)
	{
		this.disconnectCallback = callback;
		this.disconnectFired.set(false);
	}

	protected void fireDisconnect()
	{
		if(disconnectFired.compareAndSet(false, true))
		{
			final Runnable cb = disconnectCallback;
			if(cb != null) cb.run();
		}
	}

	public MidiDeviceInfo getMidiDeviceInfo()
	{
		return midiDeviceInfo;
	}

	public abstract void sendMidiMessage(MidiMessage midiEvent);

	public abstract void closeDevice() throws Exception;

	public abstract boolean isOpen();

	public abstract boolean isModeSupported(Midi.Mode mode);

	public MidiInputPublisher getPublisher()
	{
		return publisher;
	}
}
