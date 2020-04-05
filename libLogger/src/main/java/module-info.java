module de.thecodelabs.libLogger {

	requires de.thecodelabs.libStorage;
	requires org.fusesource.jansi;

	exports de.thecodelabs.logger;

	opens de.thecodelabs.logger to de.thecodelabs.libStorage;
}