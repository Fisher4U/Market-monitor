package com.qinyadan.brick.monitor.agent.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.naming.ConfigurationException;

import com.qinyadan.brick.monitor.agent.HarvestService;
import com.qinyadan.brick.monitor.agent.IAgent;
import com.qinyadan.brick.monitor.agent.config.ConfigService;

public class ServiceManagerImpl extends AbstractService implements ServiceManager {

	private final ConcurrentMap<String, Service> services = new ConcurrentHashMap();

	private final IAgent agentService;

	public ServiceManagerImpl(IAgent agent) throws ConfigurationException {
		super(ServiceManagerImpl.class.getSimpleName());
		this.agentService = agent;
		// this.configService = ConfigServiceFactory.createConfigService();
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public Map<String, Map<String, Object>> getServicesConfiguration() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addService(Service paramService) {
		// TODO Auto-generated method stub

	}

	@Override
	public Service getService(String paramString) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ConfigService getConfigService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HarvestService getHarvestService() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doStart() throws Exception {
		this.agentService.start();
		startServices();
	}

	@Override
	protected void doStop() throws Exception {
		// TODO Auto-generated method stub

	}

	private void startServices() throws Exception {
		for (Service service : this.services.values()) {
			service.start();
		}
	}

}
