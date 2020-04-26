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
				super.afterExecute(r, t);
				if(t == null && r instanceof Future<?>)
				{
					try
					{
						Future<?> future = (Future<?>) r;
						if(future.isDone())
						{
							future.get();
						}
					}
					catch(CancellationException ce)
					{
						t = ce;
					}
					catch(ExecutionException ee)
					{
						t = ee.getCause();
					}
					catch(InterruptedException ie)
					{
						Thread.currentThread().interrupt();
					}
				}
				if(t != null)
				{
					LoggerBridge.error(t);
				}
			}
		};
		LoggerBridge.info("Start ExecutorService");
	}

	private static int task = 0;

	public static void runLater(Runnable runnable)
	{
		if(executorService == null)
		{
			task = 0;
			initWorker();
		}
		task++;
		if(ApplicationUtils.getApplication().isDebug())
		{
			LoggerBridge.trace("Submit " + task + " task");
		}
		executorService.submit(runnable, null);
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
}
