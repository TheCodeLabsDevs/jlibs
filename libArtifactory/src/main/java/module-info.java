module de.thecodelabs.libArtifactory {

	requires java.net.http;

	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;

	exports de.thecodelabs.artifactory;

	opens de.thecodelabs.artifactory to com.fasterxml.jackson.databind;
}