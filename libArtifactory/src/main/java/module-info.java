module de.thecodelabs.libArtifactory {

	requires unirest.java;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;

	exports de.thecodelabs.artifactory;

	opens de.thecodelabs.artifactory to com.fasterxml.jackson.databind;
}