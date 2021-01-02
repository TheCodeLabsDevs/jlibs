package de.thecodelabs.utils.threading;

import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.logger.LoggerBridge;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class Worker
{
	private static ExecutorService executorService;
	private static final List<AutoCloseable> closeableList;

	static
	{
		initWorker();
		closeableList = new LinkedList<>();
	}

	private Worker()
	{
	}

	private static void initWorker()
	{
		int nThreads = Runtime.getRuntime().availableProcessors();
		executorService = new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>())
		{
			@Override
			protected void afterExecute(Runnable r, Throwable t)
			{
				Worker.afterExecute(r, t);
			}
		};
		LoggerBridge.info("Start ExecutorService");
	}

	private static void afterExecute(Runnable runnable, Throwable throwable)
	{
		if(throwable == null && runnable instanceof Future<?>)
		{
			try
			{
				Future<?> future = (Future<?>) runnable;
				if(future.isDone())
				{
					future.get();
				}
			}
			catch(CancellationException ce)
			{
				throwable = ce;
			}
			catch(ExecutionException ee)
			{
				throwable = ee.getCause();
			}
			catch(InterruptedException ie)
			{
				Thread.currentThread().interrupt();
			}
		}
		if(throwable != null)
		{
			LoggerBridge.error(throwable);
		}
	}

	public static void shutdown()
	{
		if(executorService != null)
		{
			executorService.shutdown();
			if(ApplicationUtils.getApplication().isDebug())
				LoggerBridge.info("Stop ExecutorService");
			executorService = null;
		}

		closeableList.forEach(i -> {
			try
			{
				i.close();
			}
			catch(Exception e)
			{
				LoggerBridge.error(e);
			}
		});
	}

	public static void addCloseable(AutoCloseable autoCloseable)
	{
		closeableList.add(autoCloseable);
	}

	/*
	Submit methods
	 */

	public static void runLater(Runnable runnable)
	{
		if(executorService == null)
		{
			initWorker();
		}
		if(ApplicationUtils.getApplication().isDebug())
		{
			LoggerBridge.trace("Submit task: " + runnable);
		}
		executorService.submit(runnable, null);
	}

	public static void delayed(long millis, Runnable runnable)
	{
		runLater(() -> {
			try
			{
				Thread.sleep(millis);
				runnable.run();
			}
			catch(InterruptedException e)
			{
				LoggerBridge.error(e);
			}
		});
	}
}
