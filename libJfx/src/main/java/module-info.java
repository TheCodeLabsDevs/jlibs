module de.thecodelabs.libJfx {
	requires javafx.base;
	requires javafx.controls;
	requires javafx.graphics;
	requires javafx.swing;
	requires javafx.fxml;

	requires org.controlsfx.controls;

	requires com.google.gson;
	requires de.thecodelabs.libUtils;

	requires com.sun.jna;
	requires com.sun.jna.platform;

	exports de.thecodelabs.utils.application.system;

	exports de.thecodelabs.utils.ui;
	exports de.thecodelabs.utils.ui.animation;
	exports de.thecodelabs.utils.ui.cell;
	exports de.thecodelabs.utils.ui.icon;
	exports de.thecodelabs.utils.ui.notification;
	exports de.thecodelabs.utils.ui.scene;
	exports de.thecodelabs.utils.ui.scene.input;
	exports de.thecodelabs.utils.ui.size;

	exports de.thecodelabs.utils.jfx;

	opens notification.style;
	opens notification;
	opens fonts;
	opens libraries;
}