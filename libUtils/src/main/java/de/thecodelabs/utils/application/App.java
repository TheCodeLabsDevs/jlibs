package de.thecodelabs.utils.application;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.storage.settings.UserDefaults;
import de.thecodelabs.utils.application.classpath.ClasspathResource;
import de.thecodelabs.utils.application.classpath.ClasspathResourceContainer;
import de.thecodelabs.utils.application.container.AppFileContainer;
import de.thecodelabs.utils.application.container.BackupInfo;
import de.thecodelabs.utils.application.container.ContainerPathType;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.application.remote.RemoteResource;
import de.thecodelabs.utils.application.remote.RemoteResourceContainer;
import de.thecodelabs.utils.application.remote.RemoteResourceType;
import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.StringUtils;
import javafx.application.Application;
import org.dom4j.DocumentException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public final class App
{

	/**
	 * Main Class for the application
	 */
	private Class<?> mainClass;

	/**
	 * Bundle Information
	 */
	private ApplicationInfo appInfo;
	/**
	 * File Container for the bundle
	 */
	private AppFileContainer container;
	private RemoteResourceContainer remoteResourceContainer;
	private ClasspathResourceContainer classpathResourceContainer;

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
	 * @param mainClass main app class
	 */
	public App(Class<?> mainClass)
	{
		this(Storage.load(Objects.requireNonNull(getInputStreamForApplicationFile(mainClass)), StorageTypes.YAML, ApplicationInfo.class));
		this.mainClass = mainClass;
	}

	private static InputStream getInputStreamForApplicationFile(Class<?> mainClass)
	{
		InputStream inputStream = mainClass.getClassLoader().getResourceAsStream("application.yml");
		if(inputStream == null)
		{
			inputStream = mainClass.getClassLoader().getResourceAsStream("config/application.yml");
		}
		return inputStream;
	}

	/**
	 * Create a new app with app information
	 *
	 * @param info app Info
	 */
	public App(ApplicationInfo info)
	{
		appInfo = info;
		container = new AppFileContainer(this);
		remoteResourceContainer = new RemoteResourceContainer(this);
		classpathResourceContainer = new ClasspathResourceContainer();
	}

	/**
	 * Get Bundle Infos
	 *
	 * @return Infos
	 */
	public ApplicationInfo getInfo()
	{
		return appInfo;
	}

	public UserDefaults getUserDefaults()
	{
		return userDefaults;
	}

	public String[] getArgs()
	{
		return args;
	}

	/**
	 * Get a path for a type of file
	 *
	 * @param type      filetype
	 * @param childPath path
	 * @return full path
	 */
	public Path getPath(ContainerPathType type, String... childPath)
	{
		return getPath(StringUtils.build(childPath, File.separator), type);
	}

	/**
	 * Get a path for a type of file
	 *
	 * @param path path
	 * @param type file type
	 * @return full path
	 */
	public Path getPath(String path, ContainerPathType type)
	{
		return container.getPath(path, type);
	}

	public RemoteResourceContainer getRemoteResources()
	{
		return remoteResourceContainer;
	}

	public RemoteResource getRemoteResource(RemoteResourceType remoteResourceType, String... more)
	{
		return getRemoteResources().get(remoteResourceType, more);
	}

	public ClasspathResourceContainer getClasspathResourceContainer()
	{
		return classpathResourceContainer;
	}

	public ClasspathResource getClasspathResource(String... name)
	{
		return getClasspathResourceContainer().get(name);
	}

	public boolean isDebug()
	{
		return debug;
	}

	/**
	 * Show debug infos in the console
	 *
	 * @param debug show
	 */
	public void setDebugging(boolean debug)
	{
		this.debug = debug;
	}

	@SuppressWarnings("unchecked")
	public void start(String[] args)
	{
		this.args = args;
		if(args != null)
		{
			if(args.length != 0)
			{
				if(Arrays.binarySearch(args, "--debug") >= 0)
				{
					debug = true;
					container.updatePath();
				}
			}
		}

		ApplicationUtils.getAppListeners().forEach(listener -> listener.applicationWillStart(this));

		printAppDetails();

		// Load Natives
		Path nativeFolder = getPath(PathType.NATIVE_LIBRARY);
		try
		{
			if(Files.exists(nativeFolder))
			{
				for(Path item : Files.newDirectoryStream(nativeFolder))
				{
					if(NativeLoader.isNativeLibraryFile(item))
					{
						loadNativeLibrary(item);
						println("Load Native Library: " + item);
					}
				}
			}
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}

		// Update
		if(checkLocalUpdate())
			updateFiles();

		// Save container information
		container.saveInformation();

		// Load User Defaults
		try
		{
			userDefaults = UserDefaults.load(getPath(PathType.CONFIGURATION, "UserDefaults.xml"));
			Runtime.getRuntime().addShutdownHook(new Thread(() ->
			{
				try
				{
					userDefaults.save(getPath(PathType.CONFIGURATION, "UserDefaults.xml"));
				}
				catch(Exception e)
				{
					LoggerBridge.error(e);
				}
			}));
		}
		catch(DocumentException | IOException e)
		{
			LoggerBridge.error(e);
		}

		// JavaFX App öffnen (wenn vorhanden)
		if(mainClass.getSuperclass().equals(Application.class))
		{
			Application.launch((Class<? extends Application>) mainClass, args);
		}
	}

	private void printAppDetails()
	{
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Launching App: ").append(appInfo.getName());

		if(appInfo.getVersion() != null)
		{
			stringBuilder.append(", version: ").append(appInfo.getVersion());
		}
		if(appInfo.getBuild() > 0)
		{
			stringBuilder.append(", build: ").append(appInfo.getBuild());
		}
		if(appInfo.getAuthor() != null)
		{
			stringBuilder.append(", date: ").append(appInfo.getDate());
		}

		println(stringBuilder.toString());
	}

	/**
	 * Loads a native library into the java vm.
	 *
	 * @param path full path to the library
	 */
	public void loadNativeLibrary(Path path)
	{
		try
		{
			System.load(path.toString());
		}
		catch(Exception e)
		{
			System.err.println("Unable to load native library " + path + " for reason: " + e.getMessage());
		}
	}

	private boolean checkLocalUpdate()
	{
		long oldVersion = container.getContainerInfo().getBuild();
		long newVersion = appInfo.getBuild();
		if(newVersion > oldVersion)
		{
			println("Update container from: " + oldVersion + " to: " + newVersion);
			return true;
		}
		return false;
	}

	private void backupFiles()
	{
		long time = System.currentTimeMillis();

		for(PathType type : PathType.values())
		{
			if(type.shouldBackup())
			{
				Path folder = container.getFolder(type);
				if(Files.exists(folder))
				{
					Path backupPath = container.getBackupPath(time, folder, type);
					try
					{
						Files.createDirectories(backupPath);

						Stream<Path> allFilesPathStream = Files.walk(folder);
						allFilesPathStream.forEach(t -> {
							try
							{
								String destinationPath = t.toString().replace(folder.toString(), backupPath.toString());
								Files.copy(t, Paths.get(destinationPath));
							}
							catch(IOException ignored)
							{
							}
						});
						allFilesPathStream.close();

					}
					catch(IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		container.getContainerInfo().getBackups().add(new BackupInfo(time, container.getContainerInfo().getBuild()));
	}

	private void updateFiles()
	{
		if(!debug)
			backupFiles();

		// AppFileContainer hat noch alte Versionnummer des Bundles
		long oldVersion = container.getContainerInfo().getBuild();
		long newVersion = appInfo.getBuild();

		ApplicationUtils.getUpdateServices().forEach(service ->
		{
			try
			{
				service.update(this, oldVersion, newVersion);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		});
		isUpdated = true;
		oldVersionNumber = oldVersion;
	}

	private void println(String string)
	{
		LoggerBridge.debug(string);
	}

	public AppFileContainer getContainer()
	{
		return container;
	}

	public boolean isUpdated()
	{
		return isUpdated;
	}

	public long getOldVersionNumber()
	{
		return oldVersionNumber;
	}
}
