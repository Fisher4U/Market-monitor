package com.qinyadan.brick.monitor.agent.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.qinyadan.brick.monitor.agent.HarvestService;
import com.qinyadan.brick.monitor.agent.HarvestServiceImpl;
import com.qinyadan.brick.monitor.agent.IAgent;
import com.qinyadan.brick.monitor.agent.ThreadService;
import com.qinyadan.brick.monitor.agent.config.ConfigService;
import com.qinyadan.brick.monitor.agent.config.ConfigServiceFactory;
import com.qinyadan.brick.monitor.agent.config.ConfigurationException;
import com.qinyadan.brick.monitor.agent.gc.GCService;
import com.qinyadan.brick.monitor.agent.samplers.CPUSamplerService;
import com.qinyadan.brick.monitor.agent.samplers.SamplerService;
import com.qinyadan.brick.monitor.agent.stats.StatsService;
import com.qinyadan.brick.monitor.agent.stats.StatsServiceImpl;

public class ServiceManagerImpl extends AbstractService implements ServiceManager {

	private final ConcurrentMap<String, Service> services = new ConcurrentHashMap<>();

	private final IAgent agentService;
	private volatile ConfigService configService;
	private volatile ThreadService threadService;
	private volatile HarvestService harvestService;

	private volatile StatsService statsService;

	private volatile Service gcService;
	private volatile Service cpuSamplerService;

	private volatile SamplerService samplerService;

	public ServiceManagerImpl(IAgent agent) throws ConfigurationException {
		super(ServiceManagerImpl.class.getSimpleName());
		this.agentService = agent;
		this.configService = ConfigServiceFactory.createConfigService();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Map<String, Object>> getServicesConfiguration() {
		return null;
	}

	@Override
	public void addService(Service service) {
		this.services.put(service.getName(), service);
	}

	@Override
	public Service getService(String paramString) {
		return null;
	}

	@Override
	public ConfigService getConfigService() {
		return this.configService;
	}

	@Override
	public HarvestService getHarvestService() {
		return this.harvestService;
	}

	@Override
	protected void doStart() throws Exception {

		this.agentService.start();

		this.harvestService = new HarvestServiceImpl();
		this.threadService = new ThreadService();
		this.statsService = new StatsServiceImpl();
		this.gcService = new GCService();
		this.cpuSamplerService = new CPUSamplerService();
		
		this.harvestService.start();
		this.threadService.start();
		this.gcService.start();
		this.cpuSamplerService.start();
	    this.statsService.start();
	    this.configService.start();

		startServices();
	}

	private void startServices() throws Exception {
		for (Service service : this.services.values()) {
			service.start();
		}
	}

	@Override
	protected void doStop() throws Exception {

	}

	@Override
	public ThreadService getThreadService() {
		return this.threadService;
	}

	@Override
	public IAgent getAgent() {
		return this.agentService;
	}

	@Override
	public StatsService getStatsService() {
		return this.statsService;
	}

	@Override
	public SamplerService getSamplerService() {
		return this.samplerService;
	}

}
