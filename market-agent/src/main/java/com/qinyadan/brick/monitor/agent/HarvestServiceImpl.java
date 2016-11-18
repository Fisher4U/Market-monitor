package com.qinyadan.brick.monitor.agent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;
import com.qinyadan.brick.monitor.agent.stats.StatsEngineImpl;

import io.netty.util.concurrent.DefaultThreadFactory;

public class HarvestServiceImpl extends AbstractService implements HarvestService {

	private static final Logger logger = LoggerFactory.getLogger(HarvestServiceImpl.class);

	public static final String HARVEST_THREAD_NAME = "Market Harvest Service";
	private static final long INITIAL_DELAY = 30000L;
	private static final long MIN_HARVEST_INTERVAL_IN_NANOSECONDS = TimeUnit.NANOSECONDS.convert(55L, TimeUnit.SECONDS);
	private static final long REPORTING_PERIOD_IN_MILLISECONDS = TimeUnit.MILLISECONDS.convert(60L, TimeUnit.SECONDS);
	private final ScheduledExecutorService scheduledExecutor;
	private final List<MonitorHook> harvestListeners = new CopyOnWriteArrayList();
	private final Map<IRPMService, HarvestTask> harvestTasks = new HashMap();

	protected HarvestServiceImpl(String name) {
		super(name);
		ThreadFactory threadFactory = new DefaultThreadFactory(HARVEST_THREAD_NAME, true);
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public void startHarvest(IRPMService rpmService) {
		HarvestTask harvestTask = getOrCreateHarvestTask(rpmService);
		harvestTask.start();
	}

	@Override
	public void addHarvestListener(MonitorHook paramHarvestListener) {

	}

	@Override
	public void removeHarvestListener(MonitorHook paramHarvestListener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void harvestNow() {
		List<HarvestTask> tasks = getHarvestTasks();
		for (HarvestTask task : tasks) {
			task.harvestNow();
		}
	}

	private synchronized List<HarvestTask> getHarvestTasks() {
		return new ArrayList(this.harvestTasks.values());
	}

	@Override
	protected void doStart() throws Exception {

	}

	@Override
	protected void doStop() throws Exception {
		List<HarvestTask> tasks = getHarvestTasks();
		for (HarvestTask task : tasks) {
			task.stop();
		}
		this.scheduledExecutor.shutdown();

	}

	private synchronized HarvestTask getOrCreateHarvestTask(IRPMService rpmService) {
		HarvestTask harvestTask = (HarvestTask) this.harvestTasks.get(rpmService);
		if (harvestTask == null) {
			harvestTask = new HarvestTask(rpmService);
			this.harvestTasks.put(rpmService, harvestTask);
		}
		return harvestTask;
	}

	private ScheduledFuture<?> scheduleHarvestTask(HarvestTask harvestTask) {
		return this.scheduledExecutor.scheduleAtFixedRate(harvestTask, INITIAL_DELAY,
				REPORTING_PERIOD_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
	}

	private final class HarvestTask implements Runnable {

		private final IRPMService rpmService;
		private ScheduledFuture<?> task;
		private final Lock harvestLock = new ReentrantLock();
		private StatsEngine lastStatsEngine = new StatsEngineImpl();
		private long lastHarvestStartTime;

		private HarvestTask(IRPMService rpmService) {
			this.rpmService = rpmService;
		}

		private synchronized void start() {
			if (!isRunning()) {
				stop();
				String msg = MessageFormat.format("Scheduling harvest task for {0}",
						new Object[] { this.rpmService.getApplicationName() });
				logger.info(msg);
				this.task = HarvestServiceImpl.this.scheduleHarvestTask(this);
			}
		}

		private synchronized void stop() {
			if (this.task != null) {
				logger.info(MessageFormat.format("Cancelling harvest task for {0}",
						new Object[] { this.rpmService.getApplicationName() }));
				this.task.cancel(false);
			}
		}

		private boolean isRunning() {
			if (this.task == null) {
				return false;
			}
			return (!this.task.isCancelled()) || (this.task.isDone());
		}

		@Override
		public void run() {
			try {
				if (shouldHarvest()) {
					harvest();
				}
			} catch (Exception e) {

			}
		}

		private boolean shouldHarvest() {
			return System.nanoTime() - this.lastHarvestStartTime >= HarvestServiceImpl.this.getMinHarvestInterval();
		}

		private void harvest() {
			this.harvestLock.lock();
			try {
				doHarvest();
			} catch (Throwable e) {
				logger.error("Error sending metric data for {0}: {1}",
						new Object[] { this.rpmService.getApplicationName(), e.toString() });
			} finally {
				this.harvestLock.unlock();
			}
		}

		private void doHarvest() throws Exception {

		}

		private void harvestNow() {
			String msg = MessageFormat.format("Sending metrics for {0} immediately",
					new Object[] { this.rpmService.getApplicationName() });
			logger.info(msg);
			harvest();
		}

	}

	public long getMinHarvestInterval() {
		return MIN_HARVEST_INTERVAL_IN_NANOSECONDS;
	}

}
