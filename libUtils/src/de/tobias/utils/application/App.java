package de.tobias.utils.application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.dom4j.DocumentException;

import de.tobias.utils.application.container.BackupInfo;
import de.tobias.utils.application.container.FileContainer;
import de.tobias.utils.application.container.PathType;
import de.tobias.utils.help.HelpMap;
import de.tobias.utils.settings.UserDefaults;
import de.tobias.utils.settings.YAMLSettings;
import de.tobias.utils.util.StringUtils;
import javafx.application.Application;

public class App {

	/**
	 * Main Class for the application
	 */
	private Class<?> mainClass;

	/**
	 * Bundle Information
	 */
	private ApplicationInfo appInfo;
	/**
	 * Helpmap for the bundle (if exists)
	 */
	private HelpMap helpMap;
	/**
	 * File Container for the bundle
	 */
	private FileContainer container;

	/**
	 * show debug messages in the console
	 */
	private boolean debug = false;

	/**
	 * Diese Variable speichert den Zustand, wenn das Programm aktualiert wurde und erstmals gestartet wurde.
	 */
	private boolean isUpdated = false;

	/**
	 * Dieser Wert speichert die Versionsnummer vor dem Update
	 */
	private long oldVersionNumber = -1;

	/**
	 * App Data
	 */
	private UserDefaults userDefaults;

	private String[] args;

	/**
	 * Create a new AppBundle with configuration file
	 * 
	 * @param mainClass
	 * @throws Exception
	 */
	public App(Class<?> mainClass) throws Exception {
		this(YAMLSettings.load(ApplicationInfo.class, mainClass.getClassLoader().getResource("application.yml")));
		this.mainClass = mainClass;
	}

	/**
	 * Create a new app with appinformation
	 * 
	 * @param info
	 */
	public App(ApplicationInfo info) {
		appInfo = info;
		container = new FileContainer(this);
	}

	/**
	 * Get Bundle Infos
	 * 
	 * @return Infos
	 */
	public ApplicationInfo getInfo() {
		return appInfo;
	}

	public UserDefaults getUserDefaults() {
		return userDefaults;
	}

	public String[] getArgs() {
		return args;
	}

	/**
	 * Get a path for a type of file
	 * 
	 * @param type
	 *            filetype
	 * @param childPath
	 *            path
	 * @return full path
	 */
	public Path getPath(PathType type, String... childPath) {
		return getPath(Paths.get(StringUtils.build(childPath, File.separator)), type);
	}

	/**
	 * Get a path for a type of file
	 * 
	 * @param childPath
	 *            path
	 * @param type
	 *            file type
	 * @return full path
	 */
	public Path getPath(Path childPath, PathType type) {
		Path path = container.getPath(childPath.toString(), type);
		if (debug) {
			// println("Get Path: " + path);
		}
		return path;
	}

	/**
	 * Get the Helpmap instance
	 * 
	 * @return helpmap
	 */
	public HelpMap getHelpMap() {
		return helpMap;
	}

	public boolean isDebug() {
		return debug;
	}

	/**
	 * Show debug infos in the console
	 * 
	 * @param debug
	 *            show
	 */
	public void setDebugging(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Automatic backup fof your file container
	 * 
	 * @param backup
	 */
	public void setAutoBackup(boolean backup) {
		appInfo.setBackup(backup); // TODO Implementieren
	}

	@SuppressWarnings("unchecked")
	public void start(String[] args) throws ApplicationException, URISyntaxException {
		this.args = args;
		if (args != null) {
			if (args.length != 0) {
				if (Arrays.binarySearch(args, "debug") >= 0) {
					debug = true;
					container.updatePath();
				}

				// Args: -clear -> Löscht file container
				if (args[0].equalsIgnoreCase("-clear")) {
					long start = System.currentTimeMillis();
					println("Start clearing container: " + appInfo.getName() + " (" + appInfo.getIdentifier() + ")");
					try {
						container.clear();

						println("Folder: '" + container.getContainerPath().toString() + "' cleared in: " + (System.currentTimeMillis() - start)
								+ "ms");
					} catch (IOException e) {
						println("Can't delete container folder: " + e.getLocalizedMessage());
					}

					return;

					// Args: -createBackup -> Erstellt ein Backup
				} else if (args[0].equalsIgnoreCase("-createBackup")) {
					backupFiles();
				}
			}
		}
		println("Launching App" + ": " + appInfo.getName() + ", version: " + appInfo.getVersion() + ", build: " + appInfo.getBuild());

		// Loading Resources
		loadResources(args);
		// Backup
		if (appInfo.isBackup())
			backupFiles();
		// Update
		if (checkLocalUpdate())
			updateFiles();

		// Container Informationen Speichern
		container.saveInformation();

		try {
			userDefaults = UserDefaults.load(getPath(PathType.CONFIGURATION, "UserDefaults.xml"));
			Runtime.getRuntime().addShutdownHook(new Thread(() ->
			{
				try {
					userDefaults.save(getPath(PathType.CONFIGURATION, "UserDefaults.xml"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}));
		} catch (ClassNotFoundException | DocumentException | IOException e) {
			e.printStackTrace();
		}

		// JavaFX App öffnen (wenn vorhanden)
		if (mainClass.getSuperclass().equals(Application.class)) {
			Application.launch((Class<? extends Application>) mainClass, args);
		}
	}

	private boolean checkLocalUpdate() {
		long oldVersion = container.getContainerInfo().getBuild();
		long newVersion = appInfo.getBuild();
		if (newVersion > oldVersion) {
			println("Update container from: " + oldVersion + " to: " + newVersion);
			return true;
		}
		return false;
	}

	private void loadResources(String[] args) {
		try {
			if (appInfo.getHelpmap() != null)
				helpMap = HelpMap.loadHelpMap(appInfo.getHelpmap());
		} catch (Exception e) {
			System.err.println("Can't load helpmap: " + e.getLocalizedMessage());
		}
	}

	private void backupFiles() {
		long time = System.currentTimeMillis();

		for (PathType type : PathType.values()) {
			if (type.shouldBackup()) {
				Path folder = container.getFolder(type);
				if (Files.exists(folder)) {
					Path backupPath = container.getBackupPath(time, folder, type);
					try {
						Files.createDirectories(backupPath);

						Stream<Path> allFilesPathStream = Files.walk(folder);
						Consumer<? super Path> action = new Consumer<Path>() {

							@Override
							public void accept(Path t) {
								try {
									String destinationPath = t.toString().replace(folder.toString(), backupPath.toString());
									Files.copy(t, Paths.get(destinationPath));
								} catch (FileAlreadyExistsException e) {} catch (IOException e) {}
							}
						};
						allFilesPathStream.forEach(action);
						allFilesPathStream.close();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		container.getContainerInfo().getBackups().add(new BackupInfo(time, container.getContainerInfo().getBuild()));
	}

	private void updateFiles() {
		// Backup erstellen, wenn nicht Automatisch bei Programmstart
		if (!appInfo.isBackup() && !debug)
			backupFiles();

		// FileContainer hat noch alte Versionnummer des Bundles
		long oldVersion = container.getContainerInfo().getBuild();
		long newVersion = appInfo.getBuild();

		ApplicationUtils.getServices().forEach(service ->
		{
			try {
				service.update(this, oldVersion, newVersion);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		isUpdated = true;
		oldVersionNumber = oldVersion;
	}

	private void println(String string) {
		if (debug) {
			System.out.println(string);
		}
	}

	public FileContainer getContainer() {
		return container;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public long getOldVersionNumber() {
		return oldVersionNumber;
	}
}
