package de.thecodelabs.utils.application;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import de.thecodelabs.storage.settings.UserDefaults;
import de.thecodelabs.utils.application.container.AppFileContainer;
import de.thecodelabs.utils.application.container.ContainerPathType;
import de.thecodelabs.utils.application.container.PathType;
import de.thecodelabs.utils.application.resources.classpath.ClasspathResource;
import de.thecodelabs.utils.application.resources.classpath.ClasspathResourceContainer;
import de.thecodelabs.utils.application.resources.remote.RemoteResource;
import de.thecodelabs.utils.application.resources.remote.RemoteResourceContainer;
import de.thecodelabs.utils.application.resources.remote.RemoteResourceType;
import de.thecodelabs.utils.logger.LoggerBridge;
import de.thecodelabs.utils.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

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

	@SuppressWarnings("unchecked")
	public <T extends ApplicationInfo.CustomUserInfo> T getUserInfo(Class<T> clazz)
	{
		return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{clazz}, (proxy, method, args) -> getInfo().getUserInfo().get(method.getName()));
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
				try(final DirectoryStream<Path> directoryStream = Files.newDirectoryStream(nativeFolder))
				{
					for(Path item : directoryStream)
					{
						if(NativeLoader.isNativeLibraryFile(item))
						{
							loadNativeLibrary(item);
							println("Load Native Library: " + item);
						}
					}
				}
			}
		}
		catch(IOException e1)
		{
			LoggerBridge.error(e1);
		}

		// Update
		if(checkLocalUpdate())
			updateFiles();

		// Save container information
		container.saveInformation();

		// Load User Defaults
		try
		{
			final Path userDefaultsPath = getPath(PathType.CONFIGURATION, "UserDefaults.json");
			userDefaults = UserDefaults.load(userDefaultsPath);
			Runtime.getRuntime().addShutdownHook(new Thread(() ->
			{
				try
				{
					userDefaults.save(userDefaultsPath);
				}
				catch(Exception e)
				{
					LoggerBridge.error(e);
				}
			}));
		}
		catch(Exception e)
		{
			LoggerBridge.error(e);
		}

		// JavaFX App öffnen (wenn vorhanden)
		try
		{
			Class<?> applicationClass = Class.forName("javafx.application.Application");
			if(mainClass.getSuperclass().equals(applicationClass))
			{
				final Method launchMethod = applicationClass.getMethod("launch", Class.class, String[].class);
				launchMethod.invoke(null, mainClass, args);
			}
		}
		catch(ReflectiveOperationException ignored)
		{
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
			LoggerBridge.error("Unable to load native library " + path + " for reason: " + e.getMessage());
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

	private void updateFiles()
	{
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
				LoggerBridge.error(e);
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
