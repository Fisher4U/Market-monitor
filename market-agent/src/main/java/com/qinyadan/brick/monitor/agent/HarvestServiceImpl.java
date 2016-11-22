package com.qinyadan.brick.monitor.agent;

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

import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;
import com.qinyadan.brick.monitor.agent.stats.StatsEngineImpl;
import com.qinyadan.brick.monitor.agent.utils.DefaultThreadFactory;


public class HarvestServiceImpl extends AbstractService implements HarvestService {

	public static final String HARVEST_THREAD_NAME = "Market Harvest Service";
	private static final long INITIAL_DELAY = 30000L;
	private static final long MIN_HARVEST_INTERVAL_IN_NANOSECONDS = TimeUnit.NANOSECONDS.convert(55L, TimeUnit.SECONDS);
	private static final long REPORTING_PERIOD_IN_MILLISECONDS = TimeUnit.MILLISECONDS.convert(60L, TimeUnit.SECONDS);
	
	private final ScheduledExecutorService scheduledExecutor;
	private final List<GatherListener> gatherListeners = new CopyOnWriteArrayList<>();

	public HarvestServiceImpl() {
		super(HarvestService.class.getSimpleName());
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
	public void addHarvestListener(GatherListener listener) {
		 this.gatherListeners.add(listener);
	}

	@Override
	public void removeHarvestListener(GatherListener listener) {
		this.gatherListeners.remove(listener);

	}

	@Override
	public void harvestNow() {
		List<HarvestTask> tasks = getHarvestTasks();
		for (HarvestTask task : tasks) {
			task.harvestNow();
		}
		task.harvestNow();
	}

	@Override
	protected void doStart() throws Exception {
		this.startHarvest(new RPMService("RPM"));
	}

	@Override
	protected void doStop() throws Exception {
		this.scheduledExecutor.shutdown();

	}

	private synchronized HarvestTask getOrCreateHarvestTask() {
		HarvestTask harvestTask = new HarvestTask();
		return harvestTask;
	}

	private ScheduledFuture<?> scheduleHarvestTask(HarvestTask harvestTask) {
		return this.scheduledExecutor.scheduleAtFixedRate(harvestTask, INITIAL_DELAY,
				REPORTING_PERIOD_IN_MILLISECONDS, TimeUnit.MILLISECONDS);
	}

	private final class HarvestTask implements Runnable {

		private ScheduledFuture<?> task;
		private final Lock harvestLock = new ReentrantLock();
		private StatsEngine lastStatsEngine = new StatsEngineImpl();
		private long lastHarvestStartTime;

		private HarvestTask() {
		}

		private synchronized void start() {
			if (!isRunning()) {
				stop();
				this.task = HarvestServiceImpl.this.scheduleHarvestTask(this);
			}
		}

		private synchronized void stop() {
			if (this.task != null) {
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
				System.out.println("$$$$"+shouldHarvest());
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
				System.out.println(e);
			} finally {
				this.harvestLock.unlock();
			}
		}

		private void doHarvest() throws Exception {
			System.out.println(">>>>"+lastStatsEngine);
		}

		private void harvestNow() {
			harvest();
		}

	}

	public long getMinHarvestInterval() {
		return MIN_HARVEST_INTERVAL_IN_NANOSECONDS;
	}

}
