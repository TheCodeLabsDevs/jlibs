import de.thecodelabs.midi.Mapping;
import de.thecodelabs.midi.action.ActionRegistry;
import de.thecodelabs.midi.device.MidiDeviceInfo;
import de.thecodelabs.midi.feedback.Feedback;
import de.thecodelabs.midi.feedback.FeedbackType;
import de.thecodelabs.midi.feedback.FeedbackValue;
import de.thecodelabs.midi.mapping.MidiKey;
import de.thecodelabs.midi.midi.Midi;
import de.thecodelabs.midi.midi.MidiCommand;
import de.thecodelabs.midi.midi.MidiCommandType;
import de.thecodelabs.midi.midi.feedback.MidiFeedbackTranscript;
import de.thecodelabs.midi.midi.feedback.MidiFeedbackTranscriptionRegistry;
import de.thecodelabs.midi.serialize.MappingSerializer;

import java.nio.file.Paths;

public class MainTest
{
	public static void main(String[] args)
	{
		try
		{
			ActionRegistry.registerActionHandler(new EimerActionHandler());
			Mapping mapping = MappingSerializer.load(Paths.get("midi.json"));

			MidiFeedbackTranscriptionRegistry.getInstance().register("Launchpad MK2", new LaunchPadTranscript());

			Midi.setUseNative(true);
			Mapping.setCurrentMapping(mapping);

			final Midi instance = Midi.getInstance();
			instance.openDevice(new MidiDeviceInfo("Launchpad MK2", "Launchpad MK2", ""), Midi.Mode.INPUT, Midi.Mode.OUTPUT);

			Midi.getInstance().showFeedback();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	private static class LaunchPadTranscript implements MidiFeedbackTranscript
	{
		@Override
		public void sendFeedback(MidiKey midiKey, FeedbackType feedbackType)
		{
			Feedback feedback = midiKey.getFeedbackForType(feedbackType);
			if(feedback == null)
			{
				return;
			}

			MidiCommand midiCommand = new MidiCommand(MidiCommandType.NOTE_ON, feedback.getChannel(), midiKey.getValue(), feedback.getValue());
			Midi.getInstance().sendMessage(midiCommand);
		}

		@Override
		public FeedbackValue[] getFeedbackValues()
		{
			return new FeedbackValue[0];
		}

		@Override
		public void clearFeedback()
		{
			final int maxMainKeyNumber = 89;

			for(byte i = 11; i <= maxMainKeyNumber; i++)
			{
				// Node_On = 144
				MidiCommand midiCommand = new MidiCommand(MidiCommandType.NOTE_ON, i, (byte) 0);
				Midi.getInstance().sendMessage(midiCommand);
			}

			// Obere Reihe an Tasten
			final int liveKeyMin = 104;
			final int liveKeyMax = 111;

			for(byte i = liveKeyMin; i <= liveKeyMax; i++)
			{
				// Control_Change = 176
				MidiCommand midiCommand = new MidiCommand(MidiCommandType.CONTROL_CHANGE, i, (byte) 0);
				Midi.getInstance().sendMessage(midiCommand);
			}
		}
	}
}
