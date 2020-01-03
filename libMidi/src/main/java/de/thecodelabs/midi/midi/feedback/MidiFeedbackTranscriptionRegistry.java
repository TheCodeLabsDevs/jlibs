package de.thecodelabs.midi.midi.feedback;

import java.util.HashMap;
import java.util.Map;

public class MidiFeedbackTranscriptionRegistry
{
	private static MidiFeedbackTranscriptionRegistry mInstance;

	private MidiFeedbackTranscriptionRegistry()
	{
		registry = new HashMap<>();
	}

	public static MidiFeedbackTranscriptionRegistry getInstance()
	{
		if(mInstance == null)
		{
			mInstance = new MidiFeedbackTranscriptionRegistry();
		}
		return mInstance;
	}

	private Map<String, MidiFeedbackTranscript> registry;

	public void register(String deviceName, MidiFeedbackTranscript transcriber)
	{
		registry.put(deviceName, transcriber);
	}

	public MidiFeedbackTranscript getTransripter(String name)
	{
		return registry.get(name);
	}
}
