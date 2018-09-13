package de.tobias.midi.serialize;

import com.google.gson.*;
import de.tobias.midi.mapping.Key;
import de.tobias.midi.mapping.KeyRegistry;
import de.tobias.midi.mapping.KeyType;

import java.lang.reflect.Type;

public class KeySerializer implements JsonDeserializer<Key>, JsonSerializer<Key>
{
	private static final String CLASS_META_KEY = "type";

	@Override
	public Key deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject jsonObj = jsonElement.getAsJsonObject();
		KeyType className = KeyType.valueOf(jsonObj.get(CLASS_META_KEY).getAsString());
		Class<? extends Key> clz = KeyRegistry.getInstance().getType(className);
		return jsonDeserializationContext.deserialize(jsonElement, clz);
	}

	@Override
	public JsonElement serialize(Key object, Type type, JsonSerializationContext jsonSerializationContext)
	{
		JsonElement jsonEle = jsonSerializationContext.serialize(object, object.getClass());
		jsonEle.getAsJsonObject().addProperty(CLASS_META_KEY, KeyRegistry.getInstance().getIdentifier(object).name());
		return jsonEle;
	}

}
