module de.thecodelabs.libStorage {

	requires com.google.gson;
	requires org.yaml.snakeyaml;
	requires java.xml;

	exports de.thecodelabs.storage;
	exports de.thecodelabs.storage.serializer;
	exports de.thecodelabs.storage.settings;
	exports de.thecodelabs.storage.settings.annotation;

	opens de.thecodelabs.storage.settings to com.google.gson;
}