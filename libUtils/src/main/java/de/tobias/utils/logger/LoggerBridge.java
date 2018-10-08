package de.tobias.utils.logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class LoggerBridge {

	public static void trace(Object obj) {
		if (isLoggerAvailable()) {
			log(obj, "TRACE");
		} else {
			System.out.println(obj);
		}
	}

	public static void debug(Object obj) {
		if (isLoggerAvailable()) {
			log(obj, "DEBUG");
		} else {
			System.out.println(obj);
		}
	}

	public static void info(Object obj) {
		if (isLoggerAvailable()) {
			log(obj, "INFO");
		} else {
			System.out.println(obj);
		}
	}

    public static void warning(Object obj) {
        if (isLoggerAvailable()) {
            log(obj, "WARNING");
        } else {
            System.out.println(obj);
        }
    }


    public static void error(Object obj) {
		if (isLoggerAvailable()) {
			log(obj, "ERROR");
		} else {
			System.err.println(obj);
		}
	}

	public static void fatal(Object obj) {
		if (isLoggerAvailable()) {
			log(obj, "FATAL");
		} else {
			System.err.println(obj);
		}
	}


	private static void log(Object obj, String level) {
		try {
			Class<?> loggerClass = Class.forName("de.tobias.logger.Logger");
			Class loggerLevelClass = Class.forName("de.tobias.logger.LogLevel");

			@SuppressWarnings("unchecked") Object logLevel = Enum.valueOf(loggerLevelClass, level);
			Method logMethod = loggerClass.getDeclaredMethod("log", loggerLevelClass, Object.class, Object[].class);
			logMethod.invoke(null, logLevel, obj, new Object[0]);
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static boolean isLoggerAvailable() {
		try {
			Class<?> loggerClass = Class.forName("de.tobias.logger.Logger");
			Method isInitializedMethod = loggerClass.getDeclaredMethod("isInitialized");
			final Object result = isInitializedMethod.invoke(null);
			if (result instanceof Boolean) {
				return (Boolean) result;
			}
			return false;
		} catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
			return false;
		}
	}

}
