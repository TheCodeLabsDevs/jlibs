package de.thecodelabs.midi.mapping;

import com.fasterxml.jackson.annotation.JsonTypeName;
import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.action.ActionHandler;
import de.thecodelabs.midi.mapping.action.ActionHandlerResolver;
import de.thecodelabs.midi.mapping.feedback.FeedbackState;
import de.thecodelabs.midi.mapping.feedback.FeedbackValue;
import de.thecodelabs.midi.mapping.feedback.FeedbackValueWriter;
import de.thecodelabs.midi.mapping.feedback.FeedbackValueWriterResolver;
import de.thecodelabs.midi.mapping.input.InputKey;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.NamedType;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class MappingRegistry implements ActionHandlerResolver, FeedbackValueWriterResolver
{
	private final Set<Class<? extends Action>> actions = new LinkedHashSet<>();
	private final Map<Class<? extends Action>, ActionHandler> actionHandlerMap = new HashMap<>();
	private final Set<Class<? extends InputKey>> inputKeys = new LinkedHashSet<>();
	private final Set<Class<? extends FeedbackState>> feedbackStates = new LinkedHashSet<>();
	private final Set<Class<? extends FeedbackValue>> feedbackValues = new LinkedHashSet<>();
	private final Map<Class<? extends InputKey>, Map<Class<? extends FeedbackValue>, FeedbackValueWriter<?, ?>>> feedbackValueWriterMap = new HashMap<>();

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

	public MappingRegistry registerFeedbackState(Class<? extends FeedbackState> feedbackStateClass)
	{
		requireJsonTypeName(feedbackStateClass);
		feedbackStates.add(feedbackStateClass);
		return this;
	}

	public <I extends InputKey, V extends FeedbackValue> MappingRegistry registerFeedbackValue(Class<I> inputKeyClass, Class<V> feedbackValueClass, FeedbackValueWriter<I, V> writer)
	{
		requireJsonTypeName(feedbackValueClass);
		feedbackValues.add(feedbackValueClass);

		feedbackValueWriterMap.putIfAbsent(inputKeyClass, new HashMap<>());
		feedbackValueWriterMap.get(inputKeyClass).put(feedbackValueClass, writer);
		return this;
	}

	@Override
	public FeedbackValueWriter<?, ?> resolve(InputKey key, FeedbackValue value)
	{
		return feedbackValueWriterMap.get(key.getClass()).get(value.getClass());
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
		for(Class<? extends Action> actionClass : actions)
		{
			builder.registerSubtypes(new NamedType(actionClass));
		}
		for(Class<? extends InputKey> inputKeyClass : inputKeys)
		{
			builder.registerSubtypes(new NamedType(inputKeyClass));
		}
		for(Class<? extends FeedbackState> feedbackState : feedbackStates)
		{
			builder.registerSubtypes(new NamedType(feedbackState));
		}
		for(Class<? extends FeedbackValue> feedbackValue : feedbackValues)
		{
			builder.registerSubtypes(new NamedType(feedbackValue));
		}
		return new MappingSerializer(builder.build());
	}

	@Override
	public ActionHandler resolve(Action action)
	{
		return actionHandlerMap.get(action.getClass());
	}
}
