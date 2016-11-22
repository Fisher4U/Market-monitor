package com.qinyadan.brick.monitor.agent.samplers;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.security.AccessControlException;

import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public class ThreadSampler implements MetricSampler {

	private final ThreadMXBean threadMXBean;

	public ThreadSampler() {
		this.threadMXBean = ManagementFactory.getThreadMXBean();
	}

	@Override
	public void sample(StatsEngine statsEngine) {
		int threadCount = this.threadMXBean.getThreadCount();
		statsEngine.getStats("Threads/all").setCallCount(threadCount);
		long[] deadlockedThreadIds;
		try {
			deadlockedThreadIds = this.threadMXBean.findMonitorDeadlockedThreads();
		} catch (AccessControlException e) {
			deadlockedThreadIds = new long[0];
		}
		int deadlockCount = deadlockedThreadIds == null ? 0 : deadlockedThreadIds.length;
		statsEngine.getStats("Threads/Deadlocks/all").setCallCount(deadlockCount);
	}

}
