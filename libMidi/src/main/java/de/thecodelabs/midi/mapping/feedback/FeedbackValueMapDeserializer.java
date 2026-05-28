package de.thecodelabs.midi.mapping.feedback;

import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.deser.std.StdDeserializer;

import java.util.HashMap;
import java.util.Map;

public class FeedbackValueMapDeserializer extends StdDeserializer<Map<FeedbackState, FeedbackValue>>
{
	public FeedbackValueMapDeserializer()
	{
		super(Map.class);
	}

	@Override
	public Map<FeedbackState, FeedbackValue> deserialize(JsonParser p, DeserializationContext ctxt)
	{
		final Map<FeedbackState, FeedbackValue> result = new HashMap<>();
		while(p.nextToken() != JsonToken.END_ARRAY)
		{
			FeedbackState state = null;
			FeedbackValue feedbackValue = null;
			while(p.nextToken() != JsonToken.END_OBJECT)
			{
				String fieldName = p.currentName();
				p.nextToken();
				if("state".equals(fieldName))
				{
					state = ctxt.readValue(p, FeedbackState.class);
				}
				else if("feedbackValue".equals(fieldName))
				{
					feedbackValue = ctxt.readValue(p, FeedbackValue.class);
				}
				else
				{
					p.skipChildren();
				}
			}
			if(state != null && feedbackValue != null)
			{
				result.put(state, feedbackValue);
			}
		}
		return result;
	}
}
