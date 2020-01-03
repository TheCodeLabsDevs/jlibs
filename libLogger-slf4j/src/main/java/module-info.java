module de.thecodelabs.libLogger.slf4j {

	requires slf4j.api;
	requires java.logging;

	requires de.thecodelabs.libLogger;

	exports de.thecodelabs.logger.slf4j;
	exports org.slf4j.impl;
}