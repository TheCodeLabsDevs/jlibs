package de.thecodelabs.utils.application;

import de.thecodelabs.utils.application.update.NativeUpdateService;
import de.thecodelabs.utils.application.update.UpdateService;

import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public final class ApplicationUtils {

	private static List<UpdateService> services;
	private static App mainApplication;
	private static App sharedApplication;

	private static List<AppListener> appListeners;

	private ApplicationUtils() {
	}

	static {
		appListeners = new ArrayList<>();

		services = new ArrayList<>();
		services.add(new NativeUpdateService());
	}

	public static App registerMainApplication(Class<?> mainClass) {
		mainApplication = new App(mainClass); // Load Main Application
		return mainApplication;
	}

	/**
	 * Get the Main Application Bundle
	 *
	 * @return application bundle
	 */
	public static App getMainApplication() {
		return mainApplication;
	}

	/**
	 * Get the Default / Shared Application Bundle
	 *
	 * @return application bundle
	 */
	public static App getSharedApplication() {
		if (sharedApplication == null) { // Lazy initial
			// Default Application Info
			ApplicationInfo sharedInfo = new ApplicationInfo();
			sharedInfo.setBuild(-1);
			sharedInfo.setIdentifier("default");
			sharedInfo.setName("Shared Application");

			// Default Application
			sharedApplication = new App(sharedInfo);
		}
		return sharedApplication;
	}

	/**
	 * Get Application. If the main application is null, this returns the shared
	 * application.
	 *
	 * @return App
	 */
	public static App getApplication() {
		if (mainApplication == null) {
			return getSharedApplication();
		} else {
			return getMainApplication();
		}
	}

	/*
	Update Service
	 */

	/**
	 * Add an update service, if the app needs to update the document container.
	 *
	 * @param service new service
	 */
	public static void registerUpdateService(UpdateService service) {
		services.add(service);
	}

	/**
	 * Get all registered update services.
	 *
	 * @return
	 */
	static List<UpdateService> getUpdateServices() {
		return services;
	}

	/*
	App Listener
	 */

	public static void addAppListener(AppListener listener) {
		appListeners.add(listener);
	}

	static List<AppListener> getAppListeners() {
		return appListeners;
	}

	/*
	Utils
	 */
	public static String getPID() {
		String name = ManagementFactory.getRuntimeMXBean().getName();
		return name.substring(0, name.indexOf("@"));
	}
}
