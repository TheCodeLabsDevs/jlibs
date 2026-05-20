module de.thecodelabs.libMidi {

	requires de.thecodelabs.libUtils;

	requires java.desktop;

	requires com.google.gson;

	requires javafx.controls;

	exports de.thecodelabs.midi;
	exports de.thecodelabs.midi.action;
	exports de.thecodelabs.midi.midi.device;
	exports de.thecodelabs.midi.event;
	exports de.thecodelabs.midi.feedback;
	exports de.thecodelabs.midi.mapping;
	exports de.thecodelabs.midi.midi;
	exports de.thecodelabs.midi.midi.feedback;
	exports de.thecodelabs.midi.serialize;

	opens de.thecodelabs.midi to com.google.gson;
	opens de.thecodelabs.midi.action to com.google.gson;
	opens de.thecodelabs.midi.feedback to com.google.gson;
	opens de.thecodelabs.midi.mapping to com.google.gson;
	exports de.thecodelabs.midi.midi.message;
}