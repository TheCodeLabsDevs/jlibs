package de.thecodelabs.midi.event;

import de.thecodelabs.midi.mapping.input.InputKey;

public record KeyInputEvent(InputKey inputKey, KeyInputType keyInputType)
{
}
