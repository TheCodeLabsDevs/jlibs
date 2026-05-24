module de.thecodelabs.libMidi {

	requires de.thecodelabs.libUtils;

	requires java.desktop;
	requires javafx.controls;

	requires tools.jackson.core;
	requires tools.jackson.databind;
	requires com.fasterxml.jackson.annotation;

	exports de.thecodelabs.midi.mapping;
	exports de.thecodelabs.midi.mapping.action;
	exports de.thecodelabs.midi.mapping.input;

	exports de.thecodelabs.midi.midi.device;
	exports de.thecodelabs.midi.feedback;
	exports de.thecodelabs.midi.midi;
	exports de.thecodelabs.midi.midi.feedback;

	exports de.thecodelabs.midi.midi.message;
}