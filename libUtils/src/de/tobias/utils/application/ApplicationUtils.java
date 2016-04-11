package de.tobias.utils.application;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.serialization.ConfigurationSerialization;

import de.tobias.utils.application.container.BackupInfo;
import de.tobias.utils.application.update.HelpMapUpdateService;
import de.tobias.utils.application.update.NativeUpdateService;
import de.tobias.utils.application.update.UpdateService;

public final class ApplicationUtils {

	private static List<UpdateService> services;
	private static App mainApplication;
	private static App sharedApplication;

	private ApplicationUtils() {
	}

	static {
		services = new ArrayList<>();
		ConfigurationSerialization.registerClass(BackupInfo.class);

		
		services.add(new HelpMapUpdateService());
		services.add(new NativeUpdateService());

		// Default Application Info
		ApplicationInfo sharedInfo = new ApplicationInfo();
		sharedInfo.setBuild(-1);
		sharedInfo.setIdentifier("default");
		sharedInfo.setName("Shared Application");

		// Default Application
		sharedApplication = new App(sharedInfo);
	}

	public static App registerMainApplication(Class<?> mainClass) throws Exception {
		mainApplication = new App(mainClass); // Load Main Application
		return mainApplication;
	}

	/**
	 * Add an update service, if the app needs to update the document container.
	 * 
	 * @param service new service
	 */
	public static void registerUpdateSercive(UpdateService service) {
		services.add(service);
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
		return sharedApplication;
	}

	/**
	 * Get Apllication. If the main application is null, this returns the shared
	 * application.
	 * 
	 * @return
	 */
	public static App getApplication() {
		if (mainApplication == null) {
			return sharedApplication;
		} else {
			return mainApplication;
		}
	}

	/**
	 * Get all registered update services.
	 * 
	 * @return
	 */
	static List<UpdateService> getServices() {
		return services;
	}
}
