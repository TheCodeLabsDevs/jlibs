package de.thecodelabs.midi.mapping.action;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Action
{
}
