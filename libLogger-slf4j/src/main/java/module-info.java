module de.thecodelabs.libLogger.slf4j {

	requires slf4j.api;
	requires java.logging;
	requires jul.to.slf4j;

	requires de.thecodelabs.libLogger;

	exports de.thecodelabs.logger.slf4j;
}