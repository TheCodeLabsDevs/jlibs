package de.thecodelabs.midi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MappingCollection
{
	private List<Mapping> mappings;
	private UUID activeMapping;

	public MappingCollection()
	{
		this(new ArrayList<>());
	}

	public MappingCollection(List<Mapping> mappings)
	{
		this.mappings = mappings;
	}

	public Optional<Mapping> getActiveMapping()
	{
		return mappings.stream().filter(mapping -> mapping.getId().equals(activeMapping)).findFirst();
	}

	public void setActiveMapping(Mapping activeMapping)
	{
		this.activeMapping = activeMapping.getId();
	}

	public List<Mapping> getMappings()
	{
		return mappings;
	}

	public void addMapping(Mapping mapping)
	{
		this.mappings.add(mapping);
	}

	public void removeMapping(Mapping mapping)
	{
		this.mappings.remove(mapping);
	}

	public int count()
	{
		return mappings.size();
	}

	public boolean containsName(String name)
	{
		return mappings.parallelStream().anyMatch(mapping -> mapping.getName().equals(name));
	}
}
