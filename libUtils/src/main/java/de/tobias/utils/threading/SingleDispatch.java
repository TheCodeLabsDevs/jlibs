package de.tobias.utils.threading;

import de.tobias.utils.application.ApplicationUtils;
import de.tobias.utils.logger.LoggerBridge;

import java.util.concurrent.*;

public class SingleDispatch {

	private static ExecutorService executorService;

	static {
		initWorker();
	}

	private static void initWorker() {
		int nThreads = 1;
		executorService = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>()) {

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

		LoggerBridge.debug("Start SingleDispatch");
	}

	private static int task = 0;

	public static void runLater(Runnable runnable) {
		if (executorService == null) {
			task = 0;
			initWorker();
		}
		task++;
		if (ApplicationUtils.getApplication().isDebug()) {
			LoggerBridge.trace("Submit " + task + " task");
		}
		executorService.submit(runnable, null);
	}

	public static void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
			LoggerBridge.debug("Stop SingleDispatch");
			executorService = null;
		}
	}
}
