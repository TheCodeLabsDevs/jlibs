module de.thecodelabs.libUtils {

	requires commons.net;

	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.swing;
	requires javafx.fxml;
	requires javafx.web;

	requires org.controlsfx.controls;

	requires java.xml;
	requires java.desktop;
	requires java.sql;
	requires java.management;

	requires com.sun.jna;
	requires com.sun.jna.platform;

	requires de.thecodelabs.libStorage;

	requires de.thecodelabs.YamlStorage;
	requires dom4j;
	requires gagawa;

	requires junit;

	exports de.thecodelabs.utils.application;
	exports de.thecodelabs.utils.application.classpath;
	exports de.thecodelabs.utils.application.container;
	exports de.thecodelabs.utils.application.external;
	exports de.thecodelabs.utils.application.remote;
	exports de.thecodelabs.utils.application.system;
	exports de.thecodelabs.utils.application.update;

	exports de.thecodelabs.utils.list;
	exports de.thecodelabs.utils.logger;

	exports de.thecodelabs.utils.io;
	exports de.thecodelabs.utils.threading;

	exports de.thecodelabs.utils.util;
	exports de.thecodelabs.utils.util.win;
	exports de.thecodelabs.utils.util.zip;

	exports de.thecodelabs.utils.ui;
	exports de.thecodelabs.utils.ui.animation;
	exports de.thecodelabs.utils.ui.cell;
	exports de.thecodelabs.utils.ui.icon;
	exports de.thecodelabs.utils.ui.notification;
	exports de.thecodelabs.utils.ui.scene;
	exports de.thecodelabs.utils.ui.scene.input;
	exports de.thecodelabs.utils.ui.size;
}