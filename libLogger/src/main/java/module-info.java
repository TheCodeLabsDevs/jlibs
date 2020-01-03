module de.thecodelabs.libLogger {

	requires de.thecodelabs.libStorage;
	requires jansi;

	exports de.thecodelabs.logger;

	opens de.thecodelabs.logger to de.thecodelabs.libStorage;
}