module de.thecodelabs.libPlugins.core {
	requires de.thecodelabs.libStorage;
	requires de.thecodelabs.libUtils;

	exports de.thecodelabs.plugins;

	opens de.thecodelabs.plugins to de.thecodelabs.libStorage;
}