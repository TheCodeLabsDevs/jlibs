package de.thecodelabs.midi.mapping.action;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TestAction(String test) implements Action
{
	@JsonCreator
	public TestAction(@JsonProperty("test") String test)
	{
		this.test = test;
	}


	@Override
	public String toString()
	{
		return "TestAction{" +
		       "test='" + test + '\'' +
		       '}';
	}
}
