package com.qinyadan.brick.monitor.agent.samplers;

import com.qinyadan.brick.monitor.agent.GatherListener;
import com.qinyadan.brick.monitor.agent.config.AgentConfig;
import com.qinyadan.brick.monitor.agent.service.AbstractService;
import com.qinyadan.brick.monitor.agent.service.ServiceFactory;
import com.qinyadan.brick.monitor.agent.stats.StatsEngine;

public class CPUSamplerService extends AbstractService implements GatherListener {

	private final boolean enabled;
	private volatile AbstractCPUSampler cpuSampler;

	public CPUSamplerService() {
		super(CPUSamplerService.class.getSimpleName());
		AgentConfig config = ServiceFactory.getConfigService().getDefaultAgentConfig();
		this.enabled = config.isCpuSamplingEnabled();
	}

	protected void doStart() {
		if (this.enabled) {
			this.cpuSampler = createCPUSampler();
			if (this.cpuSampler != null) {
				ServiceFactory.getHarvestService().addHarvestListener(this);
			}
		}
	}

	protected void doStop() {
		if (this.cpuSampler != null) {
			ServiceFactory.getHarvestService().removeHarvestListener(this);
		}
	}

	public boolean isEnabled() {
		return this.enabled;
	}

	private AbstractCPUSampler createCPUSampler() {
		try {
			return new CPUHarvester();
		} catch (Exception localException1) {

		}
		return null;
	}

	@Override
	public void before(String paramString, StatsEngine statsEngine) {
		if (this.cpuSampler != null) {
			this.cpuSampler.recordCPU(statsEngine);
		}
	}

	@Override
	public void after(String paramString) {

	}

}
