package com.qinyadan.brick.monitor.agent;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.qinyadan.brick.monitor.agent.Threads.BasicThreadInfo;
import com.qinyadan.brick.monitor.agent.Threads.ThreadNameNormalizer;
import com.qinyadan.brick.monitor.agent.Threads.ThreadNames;
import com.qinyadan.brick.monitor.agent.config.AgentConfig;
import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.service.ServiceFactory;

public class ThreadService extends AbstractService implements ThreadNames {

	private final Map<Long, Boolean> agentThreadIds;
	private volatile ThreadNameNormalizer threadNameNormalizer;

	public ThreadService() {
		super(ThreadService.class.getSimpleName());
		this.agentThreadIds = new ConcurrentHashMap<>(6);
	}

	protected void doStart() throws Exception {
		this.threadNameNormalizer = new ThreadNameNormalizer(ServiceFactory.getConfigService().getDefaultAgentConfig(),
				this);
		AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
		if (((Boolean) config.getProperty("thread_sampler.enabled", Boolean.TRUE)).booleanValue()) {
			long sampleDelayInSeconds = ((Integer) config.getProperty("thread_sampler.sample_delay_in_seconds",
					Integer.valueOf(60))).intValue();
			long samplePeriodInSeconds = ((Integer) config.getProperty("thread_sampler.sample_period_in_seconds",
					Integer.valueOf(60))).intValue();
			if (samplePeriodInSeconds > 0L) {
			} else {
			}
		} else {
		}
	}

	protected void doStop() throws Exception {
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean isCurrentThreadAnAgentThread() {
		return Thread.currentThread() instanceof AgentThread;
	}

	public boolean isAgentThreadId(Long threadId) {
		return this.agentThreadIds.containsKey(threadId);
	}

	public ThreadNameNormalizer getThreadNameNormalizer() {
		return this.threadNameNormalizer;
	}


	public Set<Long> getAgentThreadIds() {
		return Collections.unmodifiableSet(this.agentThreadIds.keySet());
	}

	public void registerAgentThreadId(long id) {
		this.agentThreadIds.put(Long.valueOf(id), Boolean.TRUE);
	}

	public static abstract interface AgentThread {
	}

	@Override
	public String getThreadName(BasicThreadInfo basicThreadInfo) {
		return basicThreadInfo.getName();
	}

}
