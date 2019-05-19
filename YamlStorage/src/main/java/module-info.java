module de.thecodelabs.YamlStorage {

	requires commons.lang;
	requires snakeyaml;
	requires com.google.common;

	requires java.logging;

	exports org.bukkit.configuration;
	exports org.bukkit.configuration.file;
	exports org.bukkit.configuration.serialization;
	exports org.bukkit.util;
	exports org.bukkit.util.io;
}