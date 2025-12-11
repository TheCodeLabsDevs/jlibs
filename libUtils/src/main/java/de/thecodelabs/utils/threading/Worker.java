package de.thecodelabs.utils.threading;

import de.thecodelabs.utils.application.ApplicationUtils;
import de.thecodelabs.utils.logger.LoggerBridge;

import java.util.Collections;
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
		closeableList = Collections.synchronizedList(new LinkedList<>());
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
				((Future<?>) runnable).get();
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
			LoggerBridge.error("Task threw exception", throwable);
		}
	}

	public static void shutdown()
	{
		executorService.shutdown();
		scheduler.shutdown();
		try
		{
			if(!executorService.awaitTermination(5, TimeUnit.SECONDS))
			{
				executorService.shutdownNow();
			}
			if(!scheduler.awaitTermination(5, TimeUnit.SECONDS))
			{
				scheduler.shutdownNow();
			}
		}
		catch(InterruptedException e)
		{
			executorService.shutdownNow();
			scheduler.shutdownNow();
			Thread.currentThread().interrupt();
		}
		if(ApplicationUtils.getApplication().isDebug())
			LoggerBridge.info("Stop ExecutorService");

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

	private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

	public static void delayed(long millis, Runnable runnable)
	{
		scheduler.schedule(() -> runLater(runnable), millis, TimeUnit.MILLISECONDS);
	}
}
