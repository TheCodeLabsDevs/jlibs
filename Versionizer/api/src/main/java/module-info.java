module de.thecodelabs.versionozer.api {

	requires com.google.gson;
	requires de.thecodelabs.libArtifactory;
	requires de.thecodelabs.libUtils;
	requires de.thecodelabs.libStorage;
	requires de.thecodelabs.libJfx;

	exports de.thecodelabs.versionizer;
	exports de.thecodelabs.versionizer.service;
	exports de.thecodelabs.versionizer.model;
	exports de.thecodelabs.versionizer.config;

	opens de.thecodelabs.versionizer to com.google.gson;
	opens de.thecodelabs.versionizer.config to de.thecodelabs.libStorage;
}