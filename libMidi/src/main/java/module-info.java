module de.thecodelabs.libMidi {

	requires de.thecodelabs.libUtils;

	requires java.desktop;

	requires com.google.gson;
	requires uk.co.xfactorylibrarians.coremidi4j;

	requires javafx.controls;

	exports de.thecodelabs.midi;
	exports de.thecodelabs.midi.action;
	exports de.thecodelabs.midi.device;
	exports de.thecodelabs.midi.device.java;
	exports de.thecodelabs.midi.event;
	exports de.thecodelabs.midi.feedback;
	exports de.thecodelabs.midi.mapping;
	exports de.thecodelabs.midi.midi;
	exports de.thecodelabs.midi.midi.feedback;
	exports de.thecodelabs.midi.serialize;
}