package de.thecodelabs.logger;

import de.thecodelabs.storage.settings.Storage;
import de.thecodelabs.storage.settings.StorageTypes;
import org.fusesource.jansi.AnsiConsole;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class Logger
{

	private static final String OUT_FILE = "out.log";
	private static final String ERR_FILE = "err.log";

	private static LogLevelFilter levelFilter;
	private static List<LogFilter> filters;
	private static FileOutputOption fileOutputOption;

	private static Path baseDir;
	private static ConsoleStream outputStream;
	private static ConsoleStream errorStream;

	private static boolean initialized = false;

	private static LoggerConfig loggerConfig;

	static
	{
		filters = new LinkedList<>();
		levelFilter = LogLevelFilter.NORMAL;
	}

	public static void init(Path baseDir)
	{
		if(initialized)
		{
			return;
		}
		try
		{
			if(Files.notExists(baseDir))
				Files.createDirectories(baseDir);

			Logger.baseDir = baseDir;
			loggerConfig = loadLoggerConfig();
			initialized = true;

			// Allow Windows to use colors in prompt
			if(loggerConfig.isColorEnabled() && ConsoleUtils.isWindows() && !ConsoleUtils.runningInIntellij())
			{
				AnsiConsole.systemInstall();
			}

			setFileOutput(FileOutputOption.DISABLED, System.out, System.err);
		}
		catch(IOException e)
		{
			System.err.println("Failed to initialize logger: " + e.toString());
		}
	}

	public static void appInfo(String appName, String versionName, String versionCode, String versionDate)
	{
		log(LogLevel.INFO, appName + " - v" + versionName + " - (versioncode: " + versionCode + " from " + versionDate + ")");
	}


	public static LogLevelFilter getLevelFilter()
	{
		return levelFilter;
	}

	public static Path getOutPath()
	{
		return baseDir.resolve(OUT_FILE);
	}

	public static Path getErrPath()
	{
		return baseDir.resolve(ERR_FILE);
	}

	public static boolean isInitialized()
	{
		return initialized;
	}

	private static LoggerConfig loadLoggerConfig()
	{
		InputStream inputStream = Logger.class.getClassLoader().getResourceAsStream("libLogger.yml");
		if(inputStream == null)
		{
			inputStream = Logger.class.getClassLoader().getResourceAsStream("config/libLogger.yml");
		}
		if(inputStream != null)
		{
			return Storage.load(inputStream, StorageTypes.YAML, LoggerConfig.class);
		}
		else
		{
			return new LoggerConfig();
		}
	}

	public static void setFileOutput(FileOutputOption fileOutputOption)
	{
		setFileOutput(fileOutputOption, outputStream.getSource(), errorStream.getSource());
	}

	private static void setFileOutput(FileOutputOption fileOutputOption, PrintStream standardOut, PrintStream standardError)
	{
		if(initialized)
		{
			try
			{
				Path outFile, errFile;
				if(fileOutputOption == FileOutputOption.COMBINED)
				{
					outFile = baseDir.resolve(OUT_FILE);
					errFile = baseDir.resolve(OUT_FILE);
				}
				else
				{
					outFile = baseDir.resolve(OUT_FILE);
					errFile = baseDir.resolve(ERR_FILE);
				}

				outputStream = new ConsoleStream(outFile, standardOut, loggerConfig.getDefaultOutLevel());
				errorStream = new ConsoleStream(errFile, standardError, loggerConfig.getDefaultErrLevel());

				System.setOut(outputStream);
				System.setErr(errorStream);

				Logger.fileOutputOption = fileOutputOption;
			}
			catch(IOException e)
			{
				throw new RuntimeException(e);
			}

			outputStream.setFileOutput(fileOutputOption != FileOutputOption.DISABLED);
			errorStream.setFileOutput(fileOutputOption != FileOutputOption.DISABLED);
		}
	}

	public static void setLevelFilter(LogLevelFilter levelFilter)
	{
		Logger.levelFilter = levelFilter;
	}

	public static void addFilter(LogFilter f)
	{
		filters.add(f);
	}

	public static void removeFilter(LogFilter f)
	{
		filters.remove(f);
	}

	/**
	 * Logs a message into a special channel.
	 *
	 * @param level log level
	 * @param any   object to log
	 */
	public static void log(LogLevel level, Object any, Object... args)
	{
		log(true, level, null, any, args);
	}

	public static void log(LogLevel level, StackTraceElement element, Object any, Object... args)
	{
		log(true, level, element, any, args);
	}

	static void log(boolean newLine, LogLevel level, StackTraceElement element, Object any, Object... args)
	{
		if(!initialized)
		{
			System.err.println("initialize logger first (Logger.init(Path))");
			return;
		}
		if(levelFilter.acceptLevel(level))
		{

			if (element == null)
			{
				final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

				for(int i = 2; i < stackTrace.length; i++)
				{
					StackTraceElement current = stackTrace[i];
					if(!current.getClassName().contains(Logger.class.getPackage().getName()) && !current.getClassName().startsWith("java"))
					{
						element = current;
						break;
					}
				}
			}

			String message = any != null ? any.toString() : "null";
			if(args != null && args.length > 0)
			{
				message = MessageFormat.format(message, args);
			}

			LogMessage logMessage = new LogMessage(level, message, element);
			boolean cancelMessage = filters.stream().anyMatch(f -> !f.accept(logMessage));
			if(!cancelMessage)
			{
				PrintStream printStream;
				if(level == LogLevel.ERROR || level == LogLevel.FATAL)
				{
					printStream = System.err;
				}
				else
				{
					printStream = System.out;
				}

				if(newLine)
				{
					printStream.println(logMessage.buildString(loggerConfig));
				}
				else
				{
					printStream.print(logMessage.buildString(loggerConfig));
					printStream.flush();
				}
			}
		}
	}

	public static void info(Object any, Object... args)
	{
		log(LogLevel.INFO, any.toString(), args);
	}

	public static void trace(Object any, Object... args)
	{
		log(LogLevel.TRACE, any.toString(), args);
	}

	public static void debug(Object any, Object... args)
	{
		log(LogLevel.DEBUG, any.toString(), args);
	}

	public static void error(Object any, Object... args)
	{
		log(LogLevel.ERROR, any.toString(), args);
	}

	public static void warning(Object any, Object... args)
	{
		log(LogLevel.WARNING, any.toString(), args);
	}

	public static void fatal(Object any, Object... args)
	{
		log(LogLevel.FATAL, any.toString(), args);
	}

	public static void error(Throwable throwable)
	{
		log(LogLevel.ERROR, getStringFromException(throwable));
	}

	private static String getStringFromException(Throwable e)
	{
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}

	static LoggerConfig getLoggerConfig()
	{
		return loggerConfig;
	}

	public static void deleteLogfile()
	{
		try
		{
			Files.deleteIfExists(getOutPath());
			Files.deleteIfExists(getErrPath());
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	public static void clearLogFile()
	{
		FileOutputOption oldFileOutputOption = Logger.fileOutputOption;
		setFileOutput(FileOutputOption.DISABLED);

		try
		{
			Path outLog = getOutPath();
			if(Files.exists(outLog))
			{
				Files.write(outLog, new byte[0]);
			}
			Path errorLog = getOutPath();
			if(Files.exists(errorLog))
			{
				Files.write(errorLog, new byte[0]);
			}
		}
		catch(IOException e)
		{
			error("Can't clear log file(s)");
			e.printStackTrace();
		}
		setFileOutput(oldFileOutputOption);

	}

}
