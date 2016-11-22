package com.qinyadan.brick.monitor.agent.stats;

import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.service.ServiceFactory;

public class StatsServiceImpl extends AbstractService implements StatsService {

	private final ConcurrentMap<String, StatsEngineQueue> statsEngineQueues = new ConcurrentHashMap<>();
	private volatile StatsEngineQueue defaultStatsEngineQueue;
	private final String defaultAppName;

	public StatsServiceImpl() {
		super(StatsService.class.getSimpleName());
		this.defaultAppName = ServiceFactory.getConfigService().getDefaultAgentConfig().getApplicationName();
		this.defaultStatsEngineQueue = createStatsEngineQueue();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void doStatsWork(StatsWork work) {
		String appName = work.getAppName();
		boolean done = false;
		while (!done) {
			done = getOrCreateStatsEngineQueue(appName).doStatsWork(work);
		}

	}

	@Override
	public StatsEngine getStatsEngineForHarvest(String appName) {
		StatsEngineQueue oldStatsEngineQueue = replaceStatsEngineQueue(appName);
		return oldStatsEngineQueue.getStatsEngineForHarvest();
	}


	@Override
	protected void doStart() throws Exception {

	}

	@Override
	protected void doStop() throws Exception {

	}

	private static class StatsEngineQueue {
		private final Lock readLock;
		private final Lock writeLock;
		private final AtomicInteger statsEngineCount = new AtomicInteger();
		private ConcurrentLinkedQueue<StatsEngine> statsEngineQueue = new ConcurrentLinkedQueue<>();

		private StatsEngineQueue() {
			ReadWriteLock lock = new ReentrantReadWriteLock();
			this.readLock = lock.readLock();
			this.writeLock = lock.writeLock();
		}

		public boolean doStatsWork(StatsWork work) {
			if (this.readLock.tryLock()) {
				try {
					Queue<StatsEngine> statsEngineQueue = this.statsEngineQueue;
					if (statsEngineQueue == null) {
						return false;
					}
					doStatsWorkUnderLock(statsEngineQueue, work);
					return true;
				} finally {
					this.readLock.unlock();
				}
			}
			return false;
		}

		private void doStatsWorkUnderLock(Queue<StatsEngine> statsEngineQueue, StatsWork work) {
			StatsEngine statsEngine = null;
			try {
				statsEngine = (StatsEngine) statsEngineQueue.poll();
				if (statsEngine == null) {
					statsEngine = createStatsEngine();
					this.statsEngineCount.incrementAndGet();
				}
				work.doWork(statsEngine);
			} catch (Exception e) {
				
			} finally {
				if (statsEngine != null) {
					try {
						if (!statsEngineQueue.offer(statsEngine)) {
						}
					} catch (Exception e) {
					}
				}
			}
		}

		private StatsEngine createStatsEngine() {
			return new StatsEngineImpl();
		}
		
		public StatsEngine getStatsEngineForHarvest(){
			return null;
		}
	}

	private StatsEngineQueue createStatsEngineQueue() {
		return new StatsEngineQueue();
	}

	private StatsEngineQueue replaceStatsEngineQueue(String appName) {
		StatsEngineQueue oldStatsEngineQueue = getOrCreateStatsEngineQueue(appName);
		StatsEngineQueue newStatsEngineQueue = createStatsEngineQueue();
		if (oldStatsEngineQueue == this.defaultStatsEngineQueue) {
			this.defaultStatsEngineQueue = newStatsEngineQueue;
		} else {
			this.statsEngineQueues.put(appName, newStatsEngineQueue);
		}
		return oldStatsEngineQueue;
	}

	private StatsEngineQueue getOrCreateStatsEngineQueue(String appName) {
		StatsEngineQueue statsEngineQueue = getStatsEngineQueue(appName);
		if (statsEngineQueue != null) {
			return statsEngineQueue;
		}
		statsEngineQueue = createStatsEngineQueue();
		StatsEngineQueue oldStatsEngineQueue = (StatsEngineQueue) this.statsEngineQueues.putIfAbsent(appName,
				statsEngineQueue);
		return oldStatsEngineQueue == null ? statsEngineQueue : oldStatsEngineQueue;
	}

	private StatsEngineQueue getStatsEngineQueue(String appName) {
		if ((appName == null) || (appName.equals(this.defaultAppName))) {
			return this.defaultStatsEngineQueue;
		}
		return (StatsEngineQueue) this.statsEngineQueues.get(appName);
	}

}
