package de.tobias.utils.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import de.tobias.logger.LogLevel;
import de.tobias.utils.application.ApplicationUtils;

public class Worker {

	private static ExecutorService executorService;

	static {
		initWorker();
	}

	private static void initWorker() {
		int nThreads = Runtime.getRuntime().availableProcessors();
		executorService = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()) {

			@Override
			public <T> Future<T> submit(final Callable<T> task) {
				Callable<T> wrappedTask = () ->
				{
					try {
						return task.call();
					} catch (Exception e) {
						e.printStackTrace();
						throw e;
					}
				};

				return super.submit(wrappedTask);
			}

			@Override
			public <T> Future<T> submit(Runnable task, T result) {
				Runnable wrapperTask = () ->
				{
					try {
						task.run();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};

				return super.submit(wrapperTask, result);
			}
		};

		log("Start ExecutorService");
	}

	private static int task = 0;

	public static Future<Void> runLater(Runnable runnable) {
		if (executorService == null) {
			task = 0;
			initWorker();
		}
		task++;
		if (ApplicationUtils.getApplication().isDebug())
			log("Submit " + task + " task");
		Future<Void> future = executorService.submit(runnable, null);
		return future;
	}

	public static void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
			if (ApplicationUtils.getApplication().isDebug())
				log("Stop ExecutorService");
			executorService = null;
		}
	}

	private static void log(String message) {
		if (ApplicationUtils.getApplication().isDebug()) {
			try {
				Class<?> clazz = Class.forName("de.tobias.logger.Logger");
				Method method = clazz.getMethod("log", LogLevel.class, String.class);
				method.invoke(null, LogLevel.DEBUG, message);
			} catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				System.out.println(message);
			}
		}
	}
}
