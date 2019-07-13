package de.thecodelabs.midi;

import java.util.ArrayList;
import java.util.List;

public class MappingCollection
{
	private List<Mapping> mappings;

	public MappingCollection()
	{
		this(new ArrayList<>());
	}

	public MappingCollection(List<Mapping> mappings)
	{
		this.mappings = mappings;
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
		this.mappings.add(mapping);
	}
}
