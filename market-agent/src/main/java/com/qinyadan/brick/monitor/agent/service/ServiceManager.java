package com.qinyadan.brick.monitor.agent.service;

import java.util.Map;

import com.qinyadan.brick.monitor.agent.HarvestService;
import com.qinyadan.brick.monitor.agent.IAgent;
import com.qinyadan.brick.monitor.agent.ThreadService;
import com.qinyadan.brick.monitor.agent.config.ConfigService;
import com.qinyadan.brick.monitor.agent.samplers.SamplerService;
import com.qinyadan.brick.monitor.agent.stats.StatsService;

public interface ServiceManager extends Service {

	public Map<String, Map<String, Object>> getServicesConfiguration();

	public void addService(Service paramService);

	public Service getService(String paramString);

	public ConfigService getConfigService();

	public HarvestService getHarvestService();

	public ThreadService getThreadService();

	public IAgent getAgent();

	public StatsService getStatsService();

	public SamplerService getSamplerService();
}
