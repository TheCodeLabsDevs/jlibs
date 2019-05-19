module de.thecodelabs.libStorage {

	requires de.thecodelabs.YamlStorage;

	requires gson;
	requires dom4j;

	requires java.xml;

	exports de.thecodelabs.storage;
	exports de.thecodelabs.storage.document;
	exports de.thecodelabs.storage.serializer;
	exports de.thecodelabs.storage.settings;
	exports de.thecodelabs.storage.settings.annotation;
	exports de.thecodelabs.storage.xml;
}