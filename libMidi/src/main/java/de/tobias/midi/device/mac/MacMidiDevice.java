package de.tobias.midi.device.mac;

import de.tobias.midi.Midi;
import de.tobias.midi.MidiCommand;
import de.tobias.midi.MidiCommandHandler;
import de.tobias.midi.device.MidiDevice;
import de.tobias.midi.device.MidiDeviceInfo;

import javax.sound.midi.SysexMessage;
import java.util.Arrays;

public class MacMidiDevice extends MidiDevice
{
	public MacMidiDevice(MidiDeviceInfo midiDeviceInfo)
	{
		super(midiDeviceInfo);
	}

	@Override
	public void sendMidiMessage(MidiCommand midiEvent)
	{
		sendMidiMessage_N(new byte[]{});
	}

	private native void sendMidiMessage_N(byte[] data);

	@Override
	public native void closeDevice() throws Exception;

	@Override
	public native boolean isOpen();

	@Override
	public native boolean isModeSupported(Midi.Mode mode);

	@SuppressWarnings("unused")
	private static void handleMidiEvent(byte[] data, long timestamp)
	{
		if(Byte.toUnsignedInt(data[0]) == SysexMessage.SYSTEM_EXCLUSIVE)
		{
			MidiCommand midiCommand = new MidiCommand(data);
			MidiCommandHandler.getInstance().handleCommand(midiCommand);
		}
		else
		{
			for(int i = 0; i < data.length / 3; i++)
			{
				byte[] payload = new byte[3];
				System.arraycopy(data, i * 3, payload, 0, 3);
				MidiCommand midiCommand = new MidiCommand(payload);
				MidiCommandHandler.getInstance().handleCommand(midiCommand);
			}
		}
	}
}
