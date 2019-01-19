module YamlStorage {

	exports org.bukkit.configuration;
	exports org.bukkit.configuration.file;
	exports org.bukkit.configuration.serialization;

	requires java.base;
	requires java.logging;

	requires commons.lang;
	requires com.google.common;
	requires snakeyaml;
}