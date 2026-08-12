package de.thecodelabs.midi.mapping.input;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface InputKey
{
	InputKey copy();
}
