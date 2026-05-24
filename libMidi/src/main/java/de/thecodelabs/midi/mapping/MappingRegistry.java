package de.thecodelabs.midi.mapping;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.action.ActionHandler;
import de.thecodelabs.midi.mapping.action.ActionHandlerResolver;
import de.thecodelabs.midi.mapping.input.InputKey;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.NamedType;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MappingRegistry implements ActionHandlerResolver
{
	private final Set<Class<? extends Action>> actions = new LinkedHashSet<>();
	private final Map<Class<? extends Action>, ActionHandler> actionHandlerMap = new HashMap<>();
	private final Set<Class<? extends InputKey>> inputKeys = new LinkedHashSet<>();

	public MappingRegistry registerAction(Class<? extends Action> actionClass, ActionHandler actionHandler)
	{
		requireJsonTypeName(actionClass);
		actions.add(actionClass);
		actionHandlerMap.put(actionClass, actionHandler);
		return this;
	}

	public MappingRegistry registerInputKey(Class<? extends InputKey> inputKeyClass)
	{
		requireJsonTypeName(inputKeyClass);
		inputKeys.add(inputKeyClass);
		return this;
	}

	private static void requireJsonTypeName(Class<?> cls)
	{
		if(!cls.isAnnotationPresent(JsonTypeName.class))
		{
			throw new IllegalArgumentException(cls.getName() + " must be annotated with @JsonTypeName");
		}
	}

	public MappingSerializer build()
	{
		final JsonMapper.Builder builder = JsonMapper.builder();
		for (Class<? extends Action> actionClass : actions)
		{
			builder.registerSubtypes(new NamedType(actionClass));
		}
		for (Class<? extends InputKey> inputKeyClass : inputKeys)
		{
			builder.registerSubtypes(new NamedType(inputKeyClass));
		}
		return new MappingSerializer(builder.build());
	}

	@Override
	public ActionHandler resolve(Action action)
	{
		return actionHandlerMap.get(action.getClass());
	}
}
