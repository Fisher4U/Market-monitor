package com.qinyadan.brick.monitor.agent;

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

import io.netty.util.concurrent.DefaultThreadFactory;

public class HarvestServiceImpl extends AbstractService implements HarvestService {

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void doStart() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	protected void doStop() throws Exception {
		// TODO Auto-generated method stub

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
	}

}
