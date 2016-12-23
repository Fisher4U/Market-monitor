package com.qinyadan.monitor.agent.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ExecuteThreadUtil {

	private static ExecutorService executorService;

	static {
		final int maxPoolSize = 12;
		// 定义并发执行服务
		executorService = new ThreadPoolExecutor(5, maxPoolSize, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(),
				r -> {
					Thread t = new Thread(r);
					t.setName("agentThreadPool");
					return t;
				});
	}

	/**
	 * 执行线程任务
	 * 
	 * @param task
	 */
	public static void execute(Runnable task) {
		executorService.submit(task);
	}

	/**
	 * 执行线程任务
	 * 
	 * @param task
	 * @param <T>
	 * @return
	 */
	public static <T> Future<T> execute(Callable<T> task) {
		return executorService.submit(task);
	}

	/**
	 * 关闭线程池
	 */
	public static void shutdown() {
		executorService.shutdown();
	}
}
