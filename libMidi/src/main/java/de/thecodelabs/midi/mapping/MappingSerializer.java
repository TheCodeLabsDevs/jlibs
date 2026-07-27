package de.thecodelabs.midi.mapping;

import de.thecodelabs.midi.mapping.action.Action;
import de.thecodelabs.midi.mapping.input.InputKey;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.nio.file.Path;
import java.util.List;

public class MappingSerializer
{
	private final JsonMapper mapper;

	MappingSerializer(JsonMapper mapper)
	{
		this.mapper = mapper;
	}

	public record MappingEntry(InputKey inputKey, Action action) {}

	public String serialize(Mapping mapping)
	{
		return mapper.writeValueAsString(toEntries(mapping));
	}

	public void serialize(Mapping mapping, Path path)
	{
		mapper.writeValue(path, toEntries(mapping));
	}

	public Mapping deserialize(String json)
	{
		return fromEntries(mapper.readValue(json, new TypeReference<>()
		{
		}));
	}

	public Mapping deserialize(Path path)
	{
		return fromEntries(mapper.readValue(path, new TypeReference<>()
		{
		}));
	}

	private List<MappingEntry> toEntries(Mapping mapping)
	{
		return mapping.getInputKeyActionMap().entrySet().stream()
				.map(e -> new MappingEntry(e.getKey(), e.getValue()))
				.toList();
	}

	private Mapping fromEntries(List<MappingEntry> entries)
	{
		final Mapping mapping = new Mapping();
		entries.forEach(e -> mapping.addInputKeyWithAction(e.inputKey(), e.action()));
		return mapping;
	}
}
