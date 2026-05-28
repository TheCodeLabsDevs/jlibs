package de.thecodelabs.midi.mapping.feedback;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ser.std.StdSerializer;

import java.util.Map;

public class FeedbackValueMapSerializer extends StdSerializer<Map<FeedbackState, FeedbackValue>>
{
	public FeedbackValueMapSerializer()
	{
		super(Map.class);
	}

	@Override
	public void serialize(Map<FeedbackState, FeedbackValue> value, JsonGenerator gen, SerializationContext ctxt)
	{
		gen.writeStartArray();
		for(Map.Entry<FeedbackState, FeedbackValue> entry : value.entrySet())
		{
			gen.writeStartObject();
			gen.writeName("state");
			ctxt.writeValue(gen, entry.getKey());
			gen.writeName("feedbackValue");
			ctxt.writeValue(gen, entry.getValue());
			gen.writeEndObject();
		}
		gen.writeEndArray();
	}
}
