package de.thecodelabs.utils.threading;

import de.thecodelabs.utils.application.ApplicationUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Worker {

	private static ExecutorService executorService;
	private static List<AutoCloseable> closeableList;

	static {
		initWorker();
		closeableList = new LinkedList<>();
	}

	private static void initWorker() {
		int nThreads = Runtime.getRuntime().availableProcessors();
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

		System.out.println("Start ExecutorService");
	}

	private static int task = 0;

	public static void runLater(Runnable runnable) {
		if (executorService == null) {
			task = 0;
			initWorker();
		}
		task++;
		if (ApplicationUtils.getApplication().isDebug()) {
			System.out.println("Submit " + task + " task");
		}
		executorService.submit(runnable, null);
	}

	public static void shutdown() {
		if (executorService != null) {
			executorService.shutdown();
			if (ApplicationUtils.getApplication().isDebug())
				System.out.println("Stop ExecutorService");
			executorService = null;
		}

		closeableList.forEach(i -> {
			try {
				i.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	public static void addCloseable(AutoCloseable autoCloseable) {
		closeableList.add(autoCloseable);
	}
}
