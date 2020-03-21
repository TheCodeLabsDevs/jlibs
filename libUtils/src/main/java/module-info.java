module de.thecodelabs.libUtils {
	requires java.xml;
	requires java.desktop;
	requires java.sql;
	requires java.management;

	requires com.sun.jna;
	requires com.sun.jna.platform;

	requires de.thecodelabs.libStorage;

	exports de.thecodelabs.utils.application;
	exports de.thecodelabs.utils.application.container;
	exports de.thecodelabs.utils.application.resources.classpath;
	exports de.thecodelabs.utils.application.resources.remote;
	exports de.thecodelabs.utils.application.resources.external;
	exports de.thecodelabs.utils.application.update;

	exports de.thecodelabs.utils.io;

	exports de.thecodelabs.utils.list;

	exports de.thecodelabs.utils.logger;

	exports de.thecodelabs.utils.threading;

	exports de.thecodelabs.utils.util;
	exports de.thecodelabs.utils.util.localization;
	exports de.thecodelabs.utils.util.localization.formatter;
	exports de.thecodelabs.utils.util.win;
	exports de.thecodelabs.utils.util.zip;

	opens de.thecodelabs.utils.application to de.thecodelabs.libStorage;
	opens de.thecodelabs.utils.application.container to de.thecodelabs.libStorage;
}