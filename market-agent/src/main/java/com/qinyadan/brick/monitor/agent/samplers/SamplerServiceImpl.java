package com.qinyadan.brick.monitor.agent.samplers;

import java.io.Closeable;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import com.qinyadan.brick.monitor.agent.IAgent;
import com.qinyadan.brick.monitor.agent.config.AgentConfig;
import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.service.ServiceFactory;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;
import com.qinyadan.brick.monitor.agent.stats.StatsEngineImpl;
import com.qinyadan.brick.monitor.agent.stats.StatsService;
import com.qinyadan.brick.monitor.agent.stats.StatsWork;
import com.qinyadan.brick.monitor.agent.utils.DefaultThreadFactory;

public class SamplerServiceImpl extends AbstractService implements SamplerService {

	private static final String SAMPLER_THREAD_NAME = "New Relic Sampler Service";
	private static final int INITIAL_DELAY_IN_MILLISECONDS = 60000;
	private static final long DELAY_IN_MILLISECONDS = 5000L;
	private final ScheduledExecutorService scheduledExecutor;
	private final Set<ScheduledFuture<?>> tasks = new HashSet<>();
	private final StatsEngine statsEngine = new StatsEngineImpl();
	private final IAgent agent;
	private final String defaultAppName;
	private final boolean isAutoAppNamingEnabled;
	private final long memorySampleDelayInMillis;

	public SamplerServiceImpl() {
		super(SamplerService.class.getSimpleName());
		ThreadFactory threadFactory = new DefaultThreadFactory(SAMPLER_THREAD_NAME, true);
		this.scheduledExecutor = Executors.newSingleThreadScheduledExecutor(threadFactory);
		this.agent = ServiceFactory.getAgent();
		AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
		this.isAutoAppNamingEnabled = config.isAutoAppNamingEnabled();
		this.defaultAppName = config.getApplicationName();
		this.memorySampleDelayInMillis = ((Integer) config.getProperty("sampler_service.memory_sample_delay_in_millis",
				Integer.valueOf(INITIAL_DELAY_IN_MILLISECONDS))).intValue();
	}

	protected void doStart() {
		MemorySampler memorySampler = new MemorySampler();
		memorySampler.start();
		addMetricSampler(memorySampler, this.memorySampleDelayInMillis, DELAY_IN_MILLISECONDS, TimeUnit.MILLISECONDS);

		ThreadSampler threadSampler = new ThreadSampler();
		addMetricSampler(threadSampler, 60000L, 5000L, TimeUnit.MILLISECONDS);
	}

	protected void doStop() {
		synchronized (this.tasks) {
			for (ScheduledFuture<?> task : this.tasks) {
				task.cancel(false);
			}
			this.tasks.clear();
		}
		this.scheduledExecutor.shutdown();
	}

	public boolean isEnabled() {
		return true;
	}

	private void addMetricSampler(final MetricSampler sampler, long initialDelay, long delay, TimeUnit unit) {
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					SamplerServiceImpl.this.runSampler(sampler);
				} catch (Throwable t) {
					String msg = MessageFormat.format("Unable to sample {0}: {1}",
							new Object[] { getClass().getName(), t });
				} finally {
					SamplerServiceImpl.this.statsEngine.clear();
				}
			}
		};
		addSampler(runnable, initialDelay, delay, unit);
	}

	private void runSampler(MetricSampler sampler) {
		if (!this.agent.isEnabled()) {
			return;
		}
		sampler.sample(this.statsEngine);
		if (!this.isAutoAppNamingEnabled) {
			mergeStatsEngine(this.defaultAppName);
			return;
		}
		/*List<IRPMService> rpmServices = ServiceFactory.getRPMServiceManager().getRPMServices();
		for (IRPMService rpmService : rpmServices) {
			String appName = rpmService.getApplicationName();
			mergeStatsEngine(appName);
		}*/
	}

	private void mergeStatsEngine(String appName) {
		StatsService statsService = ServiceFactory.getStatsService();
		StatsWork work = new MergeStatsEngine(appName, this.statsEngine);
		statsService.doStatsWork(work);
	}
	
	@Override
	public Closeable addSampler(Runnable sampler, long period, TimeUnit timeUnit) {
		return addSampler(sampler, period, period, timeUnit);
	}
	
	@Override
	public Closeable addSampler(Runnable sampler, long initialDelay, long period, TimeUnit timeUnit) {
		if (this.scheduledExecutor.isShutdown()) {
			return null;
		}
		final ScheduledFuture<?> task = this.scheduledExecutor
				.scheduleWithFixedDelay(sampler, initialDelay, period, timeUnit);

		this.tasks.add(task);
		return new Closeable() {
			public void close() throws IOException {
				SamplerServiceImpl.this.tasks.remove(task);
				task.cancel(false);
			}
		};
	}

}
